import java.awt.event.ItemEvent;
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
 * BCDGLW
 * Breakthrough: Creatures with Breakthrough can deal extra damage to the opponent when they attack enemy creatures.
 *      If their attack damage is greater than the defending creature's defense, the excess damage is dealt to the opponent.
 * Charge: Creatures with Charge can attack the turn they are summoned.
 * Guard: Enemy creatures must attack creatures with Guard first.
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
        public String abilities = "";            // 能力描述
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

        /**
         * 是否有“突破”能力
         * @return
         */
        public boolean isBreakthrough(){
            return this.abilities.lastIndexOf('B') > 0;
        }

        /**
         * 是否有“冲锋”能力
         * @return
         */
        public boolean isCharge(){
            return this.abilities.lastIndexOf('C') > 0;
        }

        /**
         * 是否有“嘲讽”能力
         * @return
         */
        public boolean isGuard(){
            return this.abilities.lastIndexOf('G') > 0;
        }
        @Override
        public String toString() {
            return "Card{" +
                    "cardNumber=" + cardNumber +
                    ", instanceId=" + instanceId +
                    ", location=" + location +
                    ", cardType=" + cardType +
                    ", cost=" + cost +
                    ", attack=" + attack +
                    ", defense=" + defense +
                    ", abilities='" + abilities + '\'' +
                    ", myHealthChange=" + myHealthChange +
                    ", opponentHealthChange=" + opponentHealthChange +
                    ", cardDraw=" + cardDraw +
                    '}';
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

    enum PlayState {
        Draft,
        Battle;
    }

    static class GameInfo{
        PlayState playState = PlayState.Draft;
        PlayInfo player = new PlayInfo();
        PlayInfo opponenter = new PlayInfo();
        List<Card> allCards = new ArrayList<>();
        List<Card> playerHandCards = new ArrayList<>();
        List<Card> playerSideCards = new ArrayList<>();
        List<Card> opponentSideCards = new ArrayList<>();
        int opponentHand;
        int cardCount;

        @Override
        public String toString() {
            return "GameInfo{" +
                    "playState=" + playState +
                    ", player=" + player +
                    ", opponenter=" + opponenter +
                    ", allCards=" + allCards +
                    ", playerHandCards=" + playerHandCards +
                    ", playerSideCards=" + playerSideCards +
                    ", opponentSideCards=" + opponentSideCards +
                    ", opponentHand=" + opponentHand +
                    ", cardCount=" + cardCount +
                    '}';
        }
    }



    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        Random random = new Random(System.currentTimeMillis());
        // game loop
        while (true) {
            GameInfo gameInfo = new GameInfo();
            for (int i = 0; i < 2; i++) {
                PlayInfo play = i == 0 ? gameInfo.player : gameInfo.opponenter;
                play.health = in.nextInt();
                play.mana = in.nextInt();
                play.deck = in.nextInt();
                play.rune = in.nextInt();
            }
            gameInfo.playState = gameInfo.player.mana == 0 ? PlayState.Draft : PlayState.Battle;

            gameInfo.opponentHand = in.nextInt();
            gameInfo.cardCount = in.nextInt();

            for (int i = 0; i < gameInfo.cardCount; i++) {
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

                gameInfo.allCards.add(card);
                if (card.location == 0){
                    gameInfo.playerHandCards.add(card);
                }else if(card.location == 1){
                    gameInfo.playerSideCards.add(card);
                }else {
                    gameInfo.opponentSideCards.add(card);
                }
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            if (gameInfo.playState == PlayState.Draft){
                System.out.println("PICK " + random.nextInt(3));
                continue;
            }


            List<String> commandList = new ArrayList<>();

            // 召唤
            doSummon(commandList, gameInfo);
            
            if (isOpponentLose(commandList, gameInfo)){
                System.out.println(String.join(";", commandList));
                continue;
            }
            doAttack(commandList, gameInfo);
            if (commandList.isEmpty()){
                System.out.println("PASS");
            }else {
                System.out.println(String.join(";", commandList));
            }
        }
    }

    /**
     * 攻击阶段
     */
    private static void doAttack(List<String> commandList, GameInfo gameInfo) {
        // 自己场上没有怪兽， 则跳过攻击阶段
        if (gameInfo.playerSideCards.isEmpty()){
            return;
        }
        // 对面场上没有怪兽，直接攻击
        if (gameInfo.opponentSideCards.isEmpty()){
            gameInfo.playerSideCards.stream().forEach(item -> commandList.add(item.attack()));
            return;
        }

        // 处理攻击嘲讽怪
        List<Card> guardCards = gameInfo.opponentSideCards.stream().filter(Card::isGuard).collect(Collectors.toList());
        if (! guardCards.isEmpty()){
            gameInfo.opponentSideCards.removeAll(guardCards);
            guardCards = guardCards.stream().sorted((o1, o2) -> o1.defense - o2.defense).collect(Collectors.toList());
            attackGuard(commandList, gameInfo, guardCards);
            // 场上没有怪兽了，则返回
            if (gameInfo.playerSideCards.isEmpty()){
                return;
            }
            // 场上的还能再攻击的怪兽能打死对方
            if (isOpponentLose(commandList, gameInfo)){
                return;
            }
        }


        gameInfo.opponentSideCards = gameInfo.opponentSideCards.stream().sorted((o1, o2) -> {
            int score = 0;
            // 1. 把高攻低守的排前面
            if (o1.attack - o1.defense >= 3){
                score -= 10000;
            }
            if (o2.attack - o2.defense >= 3){
                score += 10000;
            }
            // 如果两只怪都是高工低守，则更高攻击的排在前面
            if (o1.attack - o1.defense >= 3 && o2.attack - o2.defense >= 3){
                score += (o2.attack - o1.attack) * 100;
            }
            // 2. 之后按照守备倒序排
            return score + o1.defense - o2.defense;
        }).collect(Collectors.toList());

        for (Card card : gameInfo.opponentSideCards){
            Optional<Card> first = gameInfo.playerSideCards.stream().filter(item -> {
                boolean result = false;
                int diff = card.attack - card.defense;
                if (diff >= 3){
                    result = result || item.attack >= card.defense;
                }else if (card.defense <= card.attack){
                    result = result || (item.attack >= card.defense && item.defense > card.attack);
                }else{
                    result = result || (item.attack >= card.defense && item.defense >= card.attack);
                }
                return result;
            }).findFirst();
            if (first.isPresent()){
                commandList.add(first.get().attack(card));
                gameInfo.playerSideCards.remove(first.get());
            }
        }

        // 还有未行动的怪兽
        if (! gameInfo.playerSideCards.isEmpty()){
            gameInfo.playerSideCards.stream().forEach(item -> commandList.add(item.attack()));
        }

    }

    /**
     * 攻击嘲讽怪
     */
    private static void attackGuard(List<String> commandList, GameInfo gameInfo, List<Card> guardCards) {
        gameInfo.playerSideCards = gameInfo.playerSideCards.stream().sorted((o1, o2) -> o1.attack - o2.attack).collect(Collectors.toList());
        do{
            Card attack = gameInfo.playerSideCards.get(0);
            Card defense = guardCards.get(0);
            commandList.add(attack.attack(defense));
            if (attack.attack >= defense.defense){
                guardCards.remove(defense);

            }
            // 不能再攻击了
            gameInfo.playerSideCards.remove(attack);
        }while (!(guardCards.isEmpty() || gameInfo.playerSideCards.isEmpty()));
    }

    /**
     * 在对面没有嘲讽怪，且自己场上的怪的总攻大于对方生命时，直接攻击
     */
    private static boolean isOpponentLose(List<String> commandList, GameInfo gameInfo) {
        // 计算场上自己场上怪攻击之和
        if (! gameInfo.playerSideCards.isEmpty()){
            long count = gameInfo.opponentSideCards.stream().filter(Card::isGuard).count();
            if (count > 0){
                return false;
            }
            int sumAttack = gameInfo.playerSideCards.stream().map(item -> item.attack).reduce(0, (sum, item) -> sum + item);
            if (sumAttack >= gameInfo.opponenter.health || gameInfo.opponentSideCards.isEmpty()){
                gameInfo.playerSideCards.stream().forEach(item -> commandList.add(item.attack()));
                return true;
            }
        }
        return false;
    }

    /**
     * 召唤怪兽
     */
    private static void doSummon(List<String> commandList,GameInfo gameInfo) {
        int mana = gameInfo.player.mana;
        do {
            int finalMana = mana;
            Optional<Card> any = gameInfo.playerHandCards.stream().filter(card -> card.cost <= finalMana).findAny();
            if (! any.isPresent()){
                System.err.println("doSummon Card: result " + any);
                return;
            }
            Card card = any.get();
            System.err.println("doSummon Card: " + card);
            if (card.isCharge()){
                gameInfo.playerSideCards.add(card);
            }
            gameInfo.playerHandCards.remove(card);
            commandList.add(card.summom());
            mana = mana - any.get().cost;
        } while (mana > 0);
    }
}
