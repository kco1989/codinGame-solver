/**
 *  * SUMMON id to summon the creature id from your hand.
 * ATTACK id1 id2 to attack creature id2 with creature id1.
 * ATTACK id -1 to attack the opponent directly with creature id.
 * PASS to do nothing this turn.
 * PICK
 * Created by lvsw on 2018/8/5.
 */
public class Card {
    public int cardNumber;              // 卡片唯一编号
    public int instanceId;              // 卡片实例id
    public int location;                //  0 在手牌  1， 在场上 2. 在对面场上
    public int cardType;                // always 0 for this league.
    public int cost;                    // 需要消耗的费用
    public int attack;                  // 攻击力
    public int defense;                 // 守备力
    public String abilities;            // 能力描述
    public int myHealthChange;          //  to be ignored in this league.
    public int opponentHealthChange;    // to be ignored in this league.
    public int cardDraw;                // to be ignored in this league.

    public String summom(){
        return "SUMMON " + this.instanceId;
    }

    public String attack(Card otherCard){
        String result = "PASS";
        if (otherCard != null){
            result = "ATTACK " + this.instanceId + " " + otherCard.instanceId;
        }else{
            result = "ATTACK " + this.instanceId + " -1";
        }
        return result;
    }

    public String attack(){
        return attack(null);
    }

}
