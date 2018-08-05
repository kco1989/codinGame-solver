import java.util.*;
import java.util.stream.Collectors;


/**
 * AI description
 * Draft phase:
 * - always pick the first card
 * Game phase:
 * - do nothing (outputs single ';')
 * SUMMON id to summon the creature id from your hand.
 * ATTACK id1 id2 to attack creature id2 with creature id1.
 * ATTACK id -1 to attack the opponent directly with creature id.
 * PASS to do nothing this turn.
 * PICK
 */

public class Player {
    static class Card {
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

    static class PlayInfo {
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

    static enum PlayState {
        Draft,
        Battle;
    }












    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        PlayState playState = PlayState.Draft;
        PlayInfo player = new PlayInfo();
        PlayInfo opponent = new PlayInfo();
        Random random = new Random(System.currentTimeMillis());
        // game loop
        while (true) {
            for (int i = 0; i < 2; i++) {
                PlayInfo play = i == 0 ? player : opponent;
                play.health = in.nextInt();
                play.mana = in.nextInt();
                play.deck = in.nextInt();
                play.rune = in.nextInt();
            }
            playState = player.mana == 0 ? PlayState.Draft : PlayState.Battle;
            System.err.println("playState -> " + playState);
            System.err.println("player -> " + player);
            System.err.println("opponent -> " + opponent);

            int opponentHand = in.nextInt();
            int cardCount = in.nextInt();
            List<Card> allCards = new ArrayList<>();
            List<Card> playerHandCards = new ArrayList<>();
            List<Card> playerSideCards = new ArrayList<>();
            List<Card> opponentSideCards = new ArrayList<>();

            for (int i = 0; i < cardCount; i++) {
                Card card = new Card();
                card.cardNumber = in.nextInt();
                card.instanceId = in.nextInt();
                card.location = in.nextInt();
                card.cardType = in.nextInt();
                card.cost = in.nextInt();
                card.attack = in.nextInt();
                card.defense = in.nextInt();
                card.abilities = in.next();
                card.myHealthChange = in.nextInt();
                card.opponentHealthChange = in.nextInt();
                card.cardDraw = in.nextInt();
                allCards.add(card);
                if (card.location == 0){
                    playerHandCards.add(card);
                }else if(card.location == 1){
                    playerSideCards.add(card);
                }else {
                    opponentSideCards.add(card);
                }
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            if (playState == PlayState.Draft){
                System.out.println("PICK " + random.nextInt(3));
                continue;
            }

            // 召唤
            List<String> list = new ArrayList<>();
            int mana = player.mana;
            do {
                int finalMana = mana;
                Optional<Card> any = playerHandCards.stream().filter(card -> card.cost <= finalMana).findAny();
                if (any.isPresent()) {
                    list.add(any.get().summom());
                    mana = mana - any.get().cost;
                } else {
                    break;
                }
            } while (mana > 0);

            // 计算场上自己场上怪攻击之和
            if (!playerSideCards.isEmpty()){
                int sumAttack = playerSideCards.stream().map(item -> item.attack).reduce(0, (sum, item) -> sum + item);
                if (sumAttack >= opponent.health || opponentSideCards.isEmpty()){
                    playerSideCards.stream().forEach(item -> list.add(item.attack()));
                    System.out.println(String.join(";", list));
                    continue;
                }
            }

            // 对面场上有怪
            if (!opponentSideCards.isEmpty()){
                opponentSideCards = opponentSideCards.stream().sorted((o1, o2) -> o1.defense - o2.defense).collect(Collectors.toList());
                for (Card card : opponentSideCards){
                    Optional<Card> first = playerSideCards.stream().filter(item -> {
                        boolean result = false;
                        if (card.defense <= card.attack){
                            result = result || (item.attack >= card.defense && item.defense > card.attack);
                        }else{
                            result = result || (item.attack >= card.defense && item.defense >= card.attack);
                        }
                        return result;
                    }).findFirst();
                    if (first.isPresent()){
                        list.add(first.get().attack(card));
                        playerSideCards.remove(first.get());
                    }
                }
            }

            // 还有未行动的怪兽
            if (! playerSideCards.isEmpty()){
                playerHandCards.stream().forEach(item -> list.add(item.attack()));
            }

            System.out.println(String.join(";", list));

        }
    }
}
