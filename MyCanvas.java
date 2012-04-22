
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/*
 * Created on 2005/07/01
 *
 * $Id: MyCanvas.java,v 1.1 2005/07/01 13:44:03 matsu Exp $
 * 
 */

/**
 * @author matsu
 * 
 */
public abstract class MyCanvas extends Canvas implements CommandListener {

    // Quit command
    Command command_exit = new Command("Quit", Command.SCREEN, 1);

    // Stage listener
    StateChangedListener listener = null;

    // Default font
    private Font font = Font.getDefaultFont();

    MyCanvas() {
        addCommand(command_exit);
    }

    /**
     * 画面のクリア
     * 
     * @param g
     */
    void clearDisplay(Graphics g) {

        g.clipRect(0, 0, Param.width, Param.height);
        g.setColor(Param.COLOR_BLACK);
        g.fillRect(0, 0, Param.width, Param.height);

    }

    /**
     * 有る一定の範囲に文字を流し込む
     * @param g
     * @param str
     * @param x
     * @param y
     * @param w
     * @param h
     * @param dwFlags
     */
    public void drawClipString(Graphics g, String str, int x, int y, int w,
            int h, int dwFlags) {
        boolean kaigyou = false, kinsoku = false;
        int pnHeight, pnWidth, i, j = 0, nowy, gyou = 0;
        char c;
        font = g.getFont();
        nowy = y;
        pnHeight = font.getHeight() / 1;// +font.getAscent();//+
        pnWidth = font.charWidth(' ');
        if (pnWidth > w * 1 || pnHeight > h * 1) {
            return;
        }
        String strbuf[] = new String[(h / pnHeight) + 1];

        while (true) {
            kaigyou = false;
            kinsoku = false;
            // 長さを測る
            for (i = 1; i < str.length() + 1; i++) {
                if ((str.substring(0, i)).indexOf("_") >= 0) {
                    str = str.substring(0, str.indexOf("_"))
                            + str.substring(str.indexOf("_") + 1, str.length());
                    kaigyou = true;
                    i--;
                    break;
                }
                if (font.stringWidth(str.substring(0, i)) > w * 1) // 長さが限界を超えた
                {
                    i--;
                    break;
                }
            }
            boolean lp = true;
            while (lp && !kaigyou && i > 0) {
                lp = false;
                if ((str.indexOf("。") == i) || (str.indexOf("っ") == i)
                        || (str.indexOf("ー") == i) || (str.indexOf("、") == i)
                        || (str.indexOf("？") == i) || (str.indexOf("！") == i)) {
                    i--;
                    lp = true;
                    kinsoku = true;
                }
            }
            if (i >= str.length() || (i == 0 && !kaigyou)) {
                strbuf[gyou] = str;
                gyou++;
                break;
            } else {
                strbuf[gyou] = str.substring(0, i);
                str = str.substring(i);
                nowy += pnHeight;
                gyou++;
            }
        }
        for (i = 0; i < gyou; i++) {
            if (dwFlags == 0) {
                drawString(g, strbuf[i], x, y + pnHeight * i);
            } else if (dwFlags == 1) {
                drawString(g, strbuf[i],
                        (x + (w - font.stringWidth(strbuf[i]) / 1) / 2), y
                                + pnHeight * i);
            } else if (dwFlags == 2) {
                drawString(g, strbuf[i], x, (h - pnHeight * gyou) / 2 + y
                        + pnHeight * i);
            } else {
                drawString(g, strbuf[i],
                        (x + (w - font.stringWidth(strbuf[i]) / 1) / 2),
                        (h - pnHeight * gyou) / 2 + y + pnHeight * i);
            }
        }
    }

    /**
     * Clipの内部呼び出し用
     * 
     * @param g
     * @param str
     * @param x
     * @param y
     * @return
     */
    private int drawString(Graphics g, String str, int x, int y) {
        g.drawString(str, x, y, Graphics.TOP | Graphics.LEFT);
        return (font.stringWidth(str));
    }

    /**
     * リスナの登録
     * 
     * @param listener
     */
    void addStateChangedListener(StateChangedListener listener) {
        this.listener = listener;
    }

}
