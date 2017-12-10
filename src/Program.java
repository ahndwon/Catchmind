import processing.core.PApplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Program extends PApplet implements ReaderListener {
    private List<Point> points = new ArrayList<>();
    static List<User> userList = new ArrayList<>();
    private String answer = "";
    private boolean isRefresh = false;
    int px;
    int py;
    private Client client;

    public static void main(String[] args) {
        PApplet.main("Program");
    }

    @Override
    public void settings() {
        size(1000, 600);
    }

    @Override
    public void setup() {
        background(255);
        client = new Client();
        try {
            client.connect(this);
            client.setUserName("Hanna Babo");
        } catch (IOException e) {
            e.printStackTrace();
            exit();
        }
    }

    @Override
    public void draw() {
        line(800, 0, 800, 600);
        line(0, 550, 800, 550);
        line(100, 550, 100, 600);
        line(700, 550, 700, 600);

        fill(0);


        noStroke();
        fill(255);
        rect(101, 551, 598, 50);


        fill(0);
        text(answer, 110, 570);


        drawMyLine();

        for (int i = 0; i < points.size(); i++) {
            point(points.get(i).getX(), points.get(i).getY());
        }

        points.clear();
        noStroke();
        fill(255);
        rect(801, 0, 200, 600);

        fill(0);
        for (int i = 0; i < userList.size(); i++) {

            if(userList.size() != 0) {

                text(userList.get(i).getUserId(), 820, 20 + 20 * i);
                text(userList.get(i).getScore(), 950, 20 + 20 * i);
            }
        }
        if(isRefresh){
            fill(255);
            rect(0,0,800,550);
            isRefresh = false;
        }


    }

    private void drawMyLine() {
        stroke(0);
        if (mousePressed) {
            float diffX = mouseX - px;
            float diffY = mouseY - py;

            float dist = (float) Math.sqrt(diffX * diffX + diffY * diffY);
            float dx = diffX / dist;
            float dy = diffY / dist;

            for (float i = 0; i < dist; i += 1f) {
                int x = (int) (px + dx * i);
                int y = (int) (py + dy * i);
                point(x, y);
                try {
                    client.setDraw("Hanna Babo", x, y);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        px = mouseX;
        py = mouseY;
    }


    @Override
    public void onTurnChange(String message) {
        String[] token = message.split("#");
        if (token[0].equals("TURN")) {
            String userId = token[1];
        }
    }

    @Override
    public void onUsers(String message) {
        String[] token = message.split("#");
        if (token[0].equals("USERS")) {
            userList.clear();
            String users = token[1];
            String[] subtoken = users.split("/");
            for (int i = 0; i < subtoken.length; i++) {
                System.out.println(subtoken[0]);
                String idScore = subtoken[i];
                String[] subsubtoken = idScore.split(",");
                userList.add(new User(subsubtoken[0], Integer.parseInt(subsubtoken[1])));
            }
        }


    }

    @Override
    public void onDraw(String message) {
        String[] token = message.split("#");
        if (token[0].equals("DRAW")) {
            String userId = token[1];
            float x = Float.parseFloat(token[2]);
            float y = Float.parseFloat(token[3]);

            points.add(new Point((int) x, (int) y));
        }
    }

    @Override
    public void onRefresh(String message) {
        if (message.equals("REFRESH")) {
            isRefresh = true;
        }
    }

    @Override
    public void onSuccess(String message) {
        String[] token = message.split("#");
        if (token[0].equals("SUCCESS")) {
            String userId = token[1];
            String keyword = token[2];
            int score = Integer.parseInt(token[3]);

            for (int i=0; i<userList.size(); i++) {
                if (userList.get(i).getUserId().equals(userId)) {
                    userList.get(i).setScore(score);
                }
            }
        }
    }

    @Override
    public void onFail(String message) {
        String[] token = message.split("#");
        if (token[0].equals("FAIL")) {
            String userId = token[1];
            String keyword = token[2];


        }

    }

    @Override
    public void onDisconnect(String message) {
        String[] token = message.split("#");
        if (token[0].equals("TURN")) {
            String userId = token[1];
        }

    }

    @Override
    public void keyPressed() {
        if (keyCode != ENTER) {
            answer += key;
        }

        if (keyCode == BACKSPACE) {
            if(answer.length()>1) {
                answer = answer.substring(0, answer.length() - 2);
            }
            System.out.println(answer);
        }

        if (keyCode == ENTER) {
            try {
                client.setAnswer(answer);
                answer="";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
