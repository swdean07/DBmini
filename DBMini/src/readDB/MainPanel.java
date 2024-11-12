package readDB;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import readDB.DAO;
import readDB.Sql;
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
    private JPanel loginPanel;
    private boolean isLoggedIn = false;

    // 생성자
    public MainPanel() {
        // 기본 프레임 설정
        setTitle("Swing Example");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙에 위치
        setLayout(new BorderLayout());

        // 로그인 패널 생성 및 추가
        loginPanel = new JPanel(new BorderLayout()); // BorderLayout 사용
        loginPanel.setBorder(BorderFactory.createEmptyBorder(100, 50, 100, 50)); // 여백 추가

        JPanel centerPanel = new JPanel(new GridBagLayout()); // 사용자명 입력 필드와 버튼을 위한 패널
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 여백 추가

        // 사용자명 입력 필드와 버튼을 중앙에 배치
        JTextField userField = new JTextField(10);
        JButton loginButton = new JButton("로그인");

        loginButton.addActionListener(e -> {
            String username = userField.getText();
            if (!username.isEmpty()) {
                isLoggedIn = true;
                loginPanel.setVisible(false);
                createButtonPanel();  // 로그인 후 버튼 패널 생성
                createMainPanel();    // 메인 화면 생성
            } else {
                JOptionPane.showMessageDialog(this, "사용자 이름을 입력하세요", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 사용자명 라벨과 입력 필드를 왼쪽에 배치
        gbc.gridx = 0; gbc.gridy = 0;
        centerPanel.add(new JLabel("사용자명: "), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        centerPanel.add(userField, gbc);

        // 로그인 버튼을 오른쪽에 배치
        gbc.gridx = 1; gbc.gridy = 1;
        centerPanel.add(loginButton, gbc);

        loginPanel.add(centerPanel, BorderLayout.CENTER); // 로그인 패널의 중앙에 centerPanel 배치

        add(loginPanel, BorderLayout.CENTER); // 화면 중앙에 로그인 패널 배치
    }

    // 로그인 후 버튼 패널 생성 메서드
    private void createButtonPanel() {
        if (isLoggedIn) {
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // 세로로 버튼 배치

            String[] buttonNames = {
                "그룹 함수 조회 예제",
                "조인 조회 예제 1",
                "조인 조회 예제 2",
                "서브쿼리 조회 예제",
                "글쓰기",
                "조회", // 조회 버튼 추가
                "삭제"  // 삭제 버튼 추가
            };

            // 버튼 추가
            for (String name : buttonNames) {
                customButton button = new customButton(name, this);
                buttonPanel.add(button);
            }

            add(buttonPanel, BorderLayout.WEST); // 왼쪽에 버튼 패널 배치
        }
    }

    // 메인 화면 생성
    private void createMainPanel() {
        setViewPanel(new JPanel(new BorderLayout()));
        add(getViewPanel(), BorderLayout.CENTER); // 오른쪽에 뷰 패널 배치
        createViewPanel(null, null);  // 기본 빈 테이블 생성
    }

    // 뷰 패널에 테이블 생성 메서드
    public void createViewPanel(List<String> columnNames, List<List<String>> data) {
        getViewPanel().removeAll(); // 기존 내용 제거

        JLabel titleLabel = new JLabel("SQL 조회 결과", SwingConstants.CENTER);
        getViewPanel().add(titleLabel, BorderLayout.NORTH); // 북쪽에 배치

        if (columnNames == null || data == null) {
            // 기본 빈 테이블
            String[] defaultColumns = {"Column 1", "Column 2", "Column 3"};
            Object[][] defaultData = {
                {"", "", ""},
                {"", "", ""},
                {"", "", ""}
            };
            JTable table = new JTable(defaultData, defaultColumns);
            getViewPanel().add(new JScrollPane(table), BorderLayout.CENTER);
        } else {
            // 실제 데이터로 테이블 생성
            DefaultTableModel model = new DefaultTableModel(data.stream()
                .map(row -> row.toArray(new String[0]))
                .toArray(String[][]::new), columnNames.toArray(new String[0]));
            JTable table = new JTable(model);
            getViewPanel().add(new JScrollPane(table), BorderLayout.CENTER);
        }

        getViewPanel().revalidate(); // 뷰 패널 갱신
        getViewPanel().repaint();
    }

    // 메인 메서드
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainPanel().setVisible(true));
    }

    public JPanel getViewPanel() {
        return viewPanel;
    }

    public void setViewPanel(JPanel viewPanel) {
        this.viewPanel = viewPanel;
    }
}

// 커스텀 버튼 클래스
class customButton extends JLabel {
    private String name;
    private MainPanel parent;
	private Object viewPanel;

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
        if (name.equals("조회")) {
            // 조회 버튼 클릭 시
            Sql sqlManager = new Sql();
            String sqlQuery = sqlManager.sql().get("조회"); // 조회에 해당하는 SQL 쿼리 가져오기
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

                // 조회 결과를 라벨로 표시
                JPanel labelPanel = new JPanel();
                labelPanel.setLayout(new GridLayout(result.size(), 1));
                for (String key : columnNames) {
                    JLabel label = new JLabel(key);
                    label.setForeground(Color.BLUE);
                    label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    label.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            // 라벨 클릭 시 해당 키에 대한 데이터 조회
                            List<String> values = result.get(key);
                            JOptionPane.showMessageDialog(parent, "값: " + String.join(", ", values), key + " 값 조회", JOptionPane.INFORMATION_MESSAGE);
                        }
                    });
                    labelPanel.add(label);
                }
                getViewPanel().add(labelPanel, BorderLayout.CENTER);
                getViewPanel().revalidate();
                getViewPanel().repaint();
            } else {
                JOptionPane.showMessageDialog(parent, "조회 결과가 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (name.equals("삭제")) {
            // 삭제 버튼 클릭 시 확인 대화상자 띄우기
            int confirmation = JOptionPane.showConfirmDialog(parent, "삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                // 삭제 처리 로직 추가
                JOptionPane.showMessageDialog(parent, "삭제가 완료되었습니다.");
            }
        } else {
            // 다른 버튼 클릭 시 기존 기능 실행
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

	private Container getViewPanel() {
        this.viewPanel = viewPanel;
		return null;
	}
}

