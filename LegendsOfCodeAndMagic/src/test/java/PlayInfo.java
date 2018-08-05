/**
 * Created by lvsw on 2018/8/5.
 */
public class PlayInfo {
    public int health ;   // 生命力
    public int mana ;     // 法力值
    public int deck;      // 剩余卡牌
    public int rune ;     // to be ignored in this league

    @Override
    public String toString() {
        return "PlayInfo{" +
                "health=" + health +
                ", mana=" + mana +
                ", deck=" + deck +
                ", rune=" + rune +
                '}';
    }
}
