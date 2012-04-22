/*
 * Created on 2005/07/01
 *
 * $Id: Param.java,v 1.2 2005/07/01 13:54:07 matsu Exp $
 * 
 */

/**
 * @author matsu
 * 
 */
public class Param {
    public final static int width = 132;
    public final static int height = 144;
    
    
    public static final int COLOR_WHITE = 0xFFFFFF;
    public static final int COLOR_BLACK = 0x000000;
    public static final int COLOR_GRAY = 0x808080;
    public static final int COLOR_RED = 0xFF0000;
    public static final int COLOR_BLUE = 0x0000FF;
    public static final int COLOR_GREEN = 0x00FF00;
    public static final int COLOR_YELLOW = 0xFFFF00;
    public static final int COLOR_PURPLE = 0xFF00FF;
    public static final int COLOR_CYAN = 0x00FFFF;
    public static final int COLOR_AQUA = 0x7F7FFF;
    
    public static final String[] TITLE_SHORT_TEXT = {"NewTron破壊！"};
    
    // 画像ID，文言
    public static final String[][][] TITLE_TEXT = 
    {
        {
            {"8","ある大学教授○○純がゴルゴ13に狙撃の依頼をしている．ある最先端研究の発表を阻止したいらしいのだ．"},
            {"8","「狙撃してくれ」と○○純．「真意は何だ？」とゴルゴ．「NewTronはまずい！」「$100000キャッシュだ」"},
            {"8","制限弾数で手に持っているNewTronをぶちこわせ！"},
        },
   };
    
    public static final String[] HELP_TEXT = 
    {
        "ゴルゴ13は超A級スナイパーである．そんなゴルゴ13になりきれるシューティングゲームを作った．",
        "ゲームの流れは，はじめにストーリーが表示されて，狙撃ゲームに入る．",
        "狙撃ゲームではカーソルキーでスコープを動かし，真ん中ボタンで狙撃！！",
        "レベルは10段階用意してあり，狙撃対象の動きがだんだん速くなる．",
        "狙撃に成功すると信頼度ポイントをもらえる．レベルが高いほど高いポイントを得られる！",
        "ゴルゴ13に「失敗」という文字は無いが，万が一失敗すると信頼度が下がる．",
    };
    
}
