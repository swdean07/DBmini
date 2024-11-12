package readDB;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainPanel extends JFrame {
    private JPanel buttonPanel;
    private JPanel viewPanel;
    private Map<String, ArrayList<String>> myMap;

    // 생성자
    public MainPanel() {
        // 기본 프레임 설정
        setTitle("Swing Example");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙에 위치
        setLayout(new BorderLayout());

        // 버튼 패널 생성 및 추가
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // 세로로 버튼 배치
        createButtonPanel(); // 버튼 패널에 버튼 추가
        add(buttonPanel, BorderLayout.WEST); // 왼쪽에 버튼 패널 배치

        // 뷰 패널 생성 및 추가
        viewPanel = new JPanel(new BorderLayout());
        add(viewPanel, BorderLayout.CENTER); // 오른쪽에 뷰 패널 배치

        // 기본 초기 상태로 빈 테이블 추가
        createViewPanel(null, null);
    }

    // 버튼 패널 생성 메서드
    private void createButtonPanel() {
        String[] buttonNames = {
            "그룹 함수 조회 예제",
            "조인 조회 예제 1",
            "조인 조회 예제 2",
            "서브쿼리 조회 예제"
        };

        for (String name : buttonNames) {
            customButton button = new customButton(name, this);
            buttonPanel.add(button);
        }
    }

    // 뷰 패널에 테이블 생성 메서드
    public void createViewPanel(List<String> columnNames, List<List<String>> data) {
        viewPanel.removeAll(); // 기존 내용 제거

        JLabel titleLabel = new JLabel("SQL 조회 결과", SwingConstants.CENTER);
        viewPanel.add(titleLabel, BorderLayout.NORTH); // 북쪽에 배치

        if (columnNames == null || data == null) {
            // 기본 빈 테이블
            String[] defaultColumns = {"Column 1", "Column 2", "Column 3"};
            Object[][] defaultData = {
                {"", "", ""},
                {"", "", ""},
                {"", "", ""}
            };
            JTable table = new JTable(defaultData, defaultColumns);
            viewPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        } else {
            // 실제 데이터로 테이블 생성
            DefaultTableModel model = new DefaultTableModel(data.stream()
                .map(row -> row.toArray(new String[0]))
                .toArray(String[][]::new), columnNames.toArray(new String[0]));
            JTable table = new JTable(model);
            viewPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        }

        viewPanel.revalidate(); // 뷰 패널 갱신
        viewPanel.repaint();
    }

    // 메인 메서드
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainPanel().setVisible(true));
    }
}

class customButton extends JLabel {
    private String name;
    private MainPanel parent;

    public customButton(String name, MainPanel parent) {
        this.name = name;
        this.parent = parent;

        // 버튼 스타일 설정
        setText(name);
        setHorizontalAlignment(SwingConstants.CENTER);
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        setOpaque(true);
        setBackground(Color.LIGHT_GRAY);
        setPreferredSize(new Dimension(150, 40));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(Color.DARK_GRAY);
                setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(Color.LIGHT_GRAY);
                setForeground(Color.BLACK);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick();
            }
        });
    }

    // 클릭 이벤트 처리
    private void handleClick() {
        Sql sqlManager = new Sql();
        String sqlQuery = sqlManager.sql().get(name); // SQL 조회
        DAO dao = new DAO();
        Map<String, ArrayList<String>> result = dao.OracleInputSql(sqlQuery); // DAO 실행 결과

        if (result != null && !result.isEmpty()) {
            // 컬럼 이름과 데이터 생성
            List<String> columnNames = new ArrayList<>(result.keySet());
            List<List<String>> data = new ArrayList<>();

            // 데이터 변환 (행 단위로 정리)
            int numRows = result.values().iterator().next().size(); // 첫 열의 크기 사용
            for (int i = 0; i < numRows; i++) {
                List<String> row = new ArrayList<>();
                for (String key : columnNames) {
                    row.add(result.get(key).get(i));
                }
                data.add(row);
            }

            // 부모 뷰 패널 갱신
            parent.createViewPanel(columnNames, data);
        } else {
            JOptionPane.showMessageDialog(parent, "조회 결과가 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
