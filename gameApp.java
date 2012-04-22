/**
 * メインクラス
 * 
 * $Id: gameApp.java,v 1.2 2005/07/01 13:54:07 matsu Exp $
 */

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;

public class gameApp extends MIDlet implements StateChangedListener {

    Display display;

    // Number of images to load
    public static int image_num = 9;

    // Array to save images
    Image img[] = new Image[image_num];

    int points = 0;

    int elapsed_time = 0;

    // State name
    int STAGE_TOP = 0;

    int STAGE_TITLE = 1;

    int STAGE_GAME = 2;

    int STAGE_RESULT = 3;

    int STAGE_HELP = 90;

    int STAGE_EXIT = 99;

    // Level
    int level = 10;

    int selected_level = 1;

    int round = 0; // 0 - 1

    // 変数受け渡しのため
    boolean hit = false;

    int remain_bullets;

    /* ===== コンストラクタ ===== */
    public gameApp() {

        // 画像ロード
        for (int i = 0; i < image_num; i++) {
            try {
                img[i] = Image.createImage("/" + i + ".png");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        display = Display.getDisplay(this);

    }

    /**
     * アプリケーション開始
     */
    public void startApp() {
        this.stateChange(STAGE_TOP);
    }

    /**
     * STAGE_TOP
     * 
     * @author matsu
     * 
     */
    public class TopCanvas extends MyCanvas implements CommandListener {

        // menu
        int current_menu = 0;

        int current_level = 1;

        boolean thread = true;

        Command command_next = new Command("Next", Command.SCREEN, 2);

        TopCanvas() {
            this.addCommand(command_next);
        }

        /**
         * 描画
         */
        protected void paint(Graphics g) {
            clearDisplay(g);

            g.setColor(Param.COLOR_WHITE);

            // banner
            g.drawImage(img[0], 0, 0, Graphics.TOP | Graphics.LEFT);

            // menu
            g.drawString("START  lv.", 25, 65, Graphics.TOP | Graphics.LEFT);
            g.drawString(String.valueOf(current_level), 105, 65, Graphics.TOP
                    | Graphics.HCENTER);
            g.drawString("USAGE", 25, 85, Graphics.TOP | Graphics.LEFT);

            g.drawString("Reliability:" + points, 20, 110, Graphics.TOP
                    | Graphics.LEFT);
            g.drawLine(5, 123, Param.width - 10, 123);

            // arrow
            g.drawImage(img[1], 15, 65 + (current_menu * 20), Graphics.TOP
                    | Graphics.LEFT);

            g.drawImage(img[2], 90, 65, Graphics.TOP | Graphics.LEFT); // L
            g.drawImage(img[1], 110, 65, Graphics.TOP | Graphics.LEFT); // R

        }

        /**
         * キー認識
         */
        protected synchronized void keyPressed(int key) {

            int action = getGameAction(key); // キーコードを変換

            switch (action) {
            case UP:
                if (current_menu == 1)
                    current_menu = 0;
                break;
            case DOWN:
                if (current_menu == 0)
                    current_menu = 1;
                break;
            case LEFT:
                if (current_menu == 0)
                    if (current_level > 1)
                        current_level--;
                break;
            case RIGHT:
                if (current_menu == 0)
                    if (current_level < level)
                        current_level++;
                break;
            }

            repaint();

        }

        /**
         * コマンド処理
         */
        public void commandAction(Command c, Displayable s) {

            if (c == command_exit) { // 終了
                listener.stateChange(STAGE_EXIT);
            } else if (c == command_next) {
                if (current_menu == 0) {
                    selected_level = current_level;
                    listener.stateChange(STAGE_TITLE);
                } else if (current_menu == 1) {
                    listener.stateChange(STAGE_HELP);
                }
            }
        }

    }

    /**
     * STAGE_TITLE
     * 
     * @author matsu
     * 
     */
    public class TitleCanvas extends MyCanvas implements CommandListener {

        Command command_next = new Command("Next", Command.SCREEN, 2);

        int page = 0; // 0 - 2

        TitleCanvas() {
            this.addCommand(command_next);
        }

        /**
         * 描画
         */
        protected void paint(Graphics g) {
            clearDisplay(g);

            g.setColor(Param.COLOR_WHITE);

            // banner
            g.drawImage(
                    img[Integer.parseInt(Param.TITLE_TEXT[round][page][0])], 0,
                    0, Graphics.TOP | Graphics.LEFT);

            // 文字

            if (page == 2)
                g.setColor(Param.COLOR_RED);
            drawClipString(g, Param.TITLE_TEXT[round][page][1], 5, 55,
                    Param.width - 10, Param.height - 10, 0);

        }

        /**
         * コマンド処理
         */
        public void commandAction(Command c, Displayable s) {

            if (c == command_exit) { // 終了
                listener.stateChange(STAGE_EXIT);
            } else if (c == command_next) {
                if (page < 2) {
                    page++;
                    repaint();
                } else {
                    listener.stateChange(STAGE_GAME);
                }
            }
        }

    }

    /**
     * STAGE_GAME
     * 
     * @author matsu
     * 
     */
    public class GameCanvas extends MyCanvas implements CommandListener,
            Runnable {

        Command command_next = new Command("Retire", Command.SCREEN, 2);

        int move_x = -268;

        int sight_x = -50;

        int sight_y = -50;

        boolean thread = true;

        long start_time;

        int elapsed_time = 0;

        GameCanvas() {
            this.addCommand(command_next);
            new Thread(this).start();
            hit = false;
            remain_bullets = 10 - selected_level + 1;
            start_time = System.currentTimeMillis();
        }

        /**
         * 描画
         */
        protected void paint(Graphics g) {

            clearDisplay(g);

            g.setColor(Param.COLOR_WHITE);

            // Background
            g.setColor(0xEEFFFF);
            g.fillRect(0, 0, Param.width, Param.height); // back

            // Rail
            g.setColor(0x990000);
            g.fillRect(0, 100, Param.width, 5); // back

            // Grass
            g.setColor(0x00CC66);
            g.fillRect(0, 102, Param.width, 50); // back

            // Train
            g.drawImage(img[5], move_x, 50, Graphics.TOP | Graphics.LEFT);

            // Spark
            if (hit) {
                g.drawImage(img[6], sight_x + 125, sight_y + 125,
                        Graphics.VCENTER | Graphics.HCENTER);
                g.setColor(Param.COLOR_RED);
                g.drawString("HIT!!", sight_x + 125, sight_y + 100,
                        Graphics.VCENTER | Graphics.HCENTER);
            }

            // Sight
            g.drawImage(img[4], sight_x, sight_y, Graphics.TOP | Graphics.LEFT);

            // Top panel
            g.setColor(Param.COLOR_WHITE);
            g.fillRect(0, 0, Param.width, 20); // back

            g.setColor(Param.COLOR_RED);
            g.drawString(Param.TITLE_SHORT_TEXT[round], 2, 4, Graphics.TOP
                    | Graphics.LEFT);
            g.drawString("Lv." + selected_level, 105, 2, Graphics.TOP
                    | Graphics.LEFT);

            // Bullet
            g.drawImage(img[3], 3, 24, Graphics.TOP | Graphics.LEFT);
            g.setColor(Param.COLOR_WHITE);
            g.drawString("x " + remain_bullets, 10, 25, Graphics.TOP
                    | Graphics.LEFT);

            // Time
            g.drawString("Time:"
                    + ((System.currentTimeMillis() - start_time) / 1000), 90,
                    25, Graphics.TOP | Graphics.LEFT);

            // 状況判定
            if (move_x > 460)
                listener.stateChange(STAGE_RESULT);

            if (remain_bullets <= 0)
                listener.stateChange(STAGE_RESULT);

            // debug
            // Center of the scope
            // g.setColor(Param.COLOR_GREEN);
            // g.fillRect(sight_x+125,sight_y+125,1,1); // back

            // Area of the book
            // g.setColor(Param.COLOR_GREEN);
            // g.fillRect(move_x+177,50+2,7,9); // back

        }

        /**
         * コマンド処理
         */
        public void commandAction(Command c, Displayable s) {

            if (c == command_exit) { // 終了
                listener.stateChange(STAGE_EXIT);
            } else if (c == command_next) {
                thread = false;
                listener.stateChange(STAGE_TOP);
            }

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Runnable#run()
         */
        public void run() {

            while (thread) {
                move_x++;
                repaint();
                // System.out.println("thread_:" + move_x);
                try {
                    Thread.sleep(100 / selected_level);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        protected synchronized void keyRepeated(int keyCode) {
            this.keyPressed(keyCode);
        }

        protected synchronized void keyPressed(int keyCode) {

            int action = getGameAction(keyCode); // キーコードを変換

            switch (action) {
            case UP:
                sight_y--;
                break;
            case DOWN:
                sight_y++;
                break;
            case LEFT:
                sight_x--;
                break;
            case RIGHT:
                sight_x++;
                break;
            case FIRE: // 発射処理 あたり判定
                // x
                if ((move_x + 177 <= sight_x + 125 && sight_x + 125 <= move_x + 177 + 7)
                        && (50 + 2 < sight_y + 125 && sight_y + 125 < 50 + 2 + 9)) {
                    thread = false;
                    hit = true;
                    repaint();
                    elapsed_time = (int) (System.currentTimeMillis() - start_time) / 1000;
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    listener.stateChange(STAGE_RESULT);
                }

                remain_bullets--;
                break;
            }

            repaint();
        }
    }

    /**
     * STAGE_HELP
     * 
     * @author matsu
     * 
     */
    public class HelpCanvas extends MyCanvas implements CommandListener {

        Command command_next = new Command("Next", Command.SCREEN, 2);

        int page = 0; // 0 - 5

        HelpCanvas() {
            this.addCommand(command_next);
        }

        /**
         * 描画
         */
        protected void paint(Graphics g) {
            clearDisplay(g);

            g.setColor(Param.COLOR_WHITE);

            // banner
            g.drawImage(img[7], 0, 0, Graphics.TOP | Graphics.LEFT);

            // 文字
            drawClipString(g, Param.HELP_TEXT[page], 5, 55, Param.width - 10,
                    Param.height - 10, 0);

        }

        /**
         * コマンド処理
         */
        public void commandAction(Command c, Displayable s) {

            if (c == command_exit) { // 終了
                listener.stateChange(STAGE_EXIT);
            } else if (c == command_next) {
                if (page < 5) {
                    page++;
                    repaint();
                } else {
                    listener.stateChange(STAGE_TOP);
                }

            }
        }

    }

    /**
     * STAGE_RESULT
     * 
     * @author matsu
     * 
     */
    public class ResultCanvas extends MyCanvas implements CommandListener {

        Command command_next = new Command("TOP", Command.SCREEN, 2);

        ResultCanvas() {
            this.addCommand(command_next);
        }

        /**
         * 描画
         */
        protected void paint(Graphics g) {
            clearDisplay(g);

            g.setColor(Param.COLOR_WHITE);

            // Title
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD,
                    Font.SIZE_LARGE));
            if (hit) {
                g.setColor(Param.COLOR_WHITE);
                g.drawString("MISSION", Param.width / 2, 10, Graphics.HCENTER
                        | Graphics.TOP);
                g.drawString("SUCCEEDED!", Param.width / 2, 40,
                        Graphics.HCENTER | Graphics.TOP);
            } else {
                g.setColor(Param.COLOR_RED);
                g.drawString("MISSION", Param.width / 2, 10, Graphics.HCENTER
                        | Graphics.TOP);
                g.drawString("FAILED!", Param.width / 2, 40, Graphics.HCENTER
                        | Graphics.TOP);
            }

            // Status

            int time_point = 0;
            int accuracy_point = 0;

            if (hit) {
                time_point = (100 - elapsed_time) * selected_level;
                accuracy_point = 500 * selected_level;
            } else {
                time_point = 0;
                accuracy_point = -1000 * selected_level;
            }

            points += (time_point + accuracy_point);

            g.setColor(Param.COLOR_WHITE);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD,
                    Font.SIZE_MEDIUM));
            g.drawString("Reliability:" + points, 10, 75, Graphics.LEFT
                    | Graphics.TOP);
            g.drawLine(5, 87, Param.width - 10, 87);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN,
                    Font.SIZE_MEDIUM));
            g.drawString("Time point:     " + time_point, 10, 90, Graphics.LEFT
                    | Graphics.TOP);
            g.drawString("Accuracy point: " + accuracy_point, 10, 105,
                    Graphics.LEFT | Graphics.TOP);

        }

        /**
         * コマンド処理
         */
        public void commandAction(Command c, Displayable s) {

            if (c == command_exit) { // 終了
                listener.stateChange(STAGE_EXIT);
            } else if (c == command_next) {
                listener.stateChange(STAGE_TOP);
            }
        }

    }

    /* ===== アプリケーション一時停止 ===== */
    public void pauseApp() {
    }

    /**
     * アプリケーション終了
     */
    public void destroyApp(boolean unconditional) {
        System.exit(0);
    }

    /*
     * 状態監視 ゲームの状態を移動する
     * 
     * @see StateChangedListener#stateChange(int)
     */
    public void stateChange(int stage) {
        if (stage == STAGE_TOP) {
            TopCanvas canvas = new TopCanvas();
            canvas.setCommandListener(canvas);
            canvas.addStateChangedListener(this);
            display.setCurrent(canvas);
        } else if (stage == STAGE_TITLE) {
            TitleCanvas canvas = new TitleCanvas();
            canvas.setCommandListener(canvas);
            canvas.addStateChangedListener(this);
            display.setCurrent(canvas);
        } else if (stage == STAGE_GAME) {
            GameCanvas canvas = new GameCanvas();
            canvas.setCommandListener(canvas);
            canvas.addStateChangedListener(this);
            display.setCurrent(canvas);
        } else if (stage == STAGE_RESULT) {
            ResultCanvas canvas = new ResultCanvas();
            canvas.setCommandListener(canvas);
            canvas.addStateChangedListener(this);
            display.setCurrent(canvas);
        } else if (stage == STAGE_HELP) {
            HelpCanvas canvas = new HelpCanvas();
            canvas.setCommandListener(canvas);
            canvas.addStateChangedListener(this);
            display.setCurrent(canvas);
        } else if (stage == STAGE_EXIT) {
            destroyApp(false);
        }

    }

}
