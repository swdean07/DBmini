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

        gbc.gridx = 0; gbc.gridy = 0;
        centerPanel.add(new JLabel("사용자명: "), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        centerPanel.add(userField, gbc);

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
                "글쓰기", // 글쓰기 버튼 추가
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

    // 글쓰기 패널 생성
    public void createWritePanel() {
        getViewPanel().removeAll(); // 기존의 뷰를 초기화

        JLabel titleLabel = new JLabel("글쓰기", SwingConstants.CENTER);
        getViewPanel().add(titleLabel, BorderLayout.NORTH);

        // 글쓰기 입력 필드
        JTextArea textArea = new JTextArea(10, 30);
        textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JButton submitButton = new JButton("제출");
        submitButton.addActionListener(e -> {
            String content = textArea.getText();
            if (!content.isEmpty()) {
                JOptionPane.showMessageDialog(this, "글이 제출되었습니다.");
                // 제출 후 텍스트 에리어 초기화
                textArea.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "내용을 입력하세요.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(submitButton);
        getViewPanel().add(scrollPane, BorderLayout.CENTER);
        getViewPanel().add(panel, BorderLayout.SOUTH);

        getViewPanel().revalidate();
        getViewPanel().repaint();
    }

    // 키 값에 대한 라벨을 화면에 표시하는 메서드
    public void displayLabelsForKeys(List<String> columnNames, Map<String, ArrayList<String>> result) {
        JPanel labelPanel = new JPanel(new GridLayout(0, 1));  // 세로로 라벨 배치
        for (String key : columnNames) {
            JLabel keyLabel = new JLabel(key, SwingConstants.CENTER);
            keyLabel.setOpaque(true);
            keyLabel.setBackground(Color.LIGHT_GRAY);
            keyLabel.setPreferredSize(new Dimension(150, 40));
            keyLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

            keyLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // 라벨 클릭 시 해당 키 값에 대한 데이터 조회
                    List<String> data = result.get(key);
                    JOptionPane.showMessageDialog(MainPanel.this,
                            "키: " + key + "\n값들: " + data, "조회 결과", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            labelPanel.add(keyLabel);
        }

        JScrollPane scrollPane = new JScrollPane(labelPanel);
        getViewPanel().add(scrollPane, BorderLayout.CENTER);
        getViewPanel().revalidate();
        getViewPanel().repaint();
    }

    public JPanel getViewPanel1() {
        return viewPanel;
    }

    public void setViewPanel1(JPanel viewPanel) {
        this.viewPanel = viewPanel;
    }

//커스텀 버튼 클래
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
     if (name.equals("글쓰기")) {
         parent.createWritePanel(); // 글쓰기 클릭 시 글쓰기 패널을 생성
     } else if (name.equals("조회")) {
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
             parent.createViewPanel(columnNames, data);  // 기본 테이블 형식으로 결과를 출력
             // 조회된 데이터의 키 값에 해당하는 라벨 출력
             parent.displayLabelsForKeys(columnNames, result);
         } else {
             JOptionPane.showMessageDialog(parent, "조회 결과가 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
         }
     } else if (name.equals("삭제")) {
         // 삭제 버튼 클릭 시
         int confirmation = JOptionPane.showConfirmDialog(parent,
                 "삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);

         if (confirmation == JOptionPane.YES_OPTION) {
             // 사용자가 "확인"을 누른 경우 삭제 작업을 수행합니다.
             performDeleteAction();
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

 // 삭제 작업을 수행하는 메서드 (여기서 실제 삭제 처리를 진행)
 private void performDeleteAction() {
     // 실제 삭제 로직을 여기서 작성하세요.
     // 예: DAO를 통해 데이터베이스에서 해당 항목을 삭제하는 작업

     JOptionPane.showMessageDialog(parent, "삭제가 완료되었습니다.", "삭제 완료", JOptionPane.INFORMATION_MESSAGE);
 	}
  }
}



