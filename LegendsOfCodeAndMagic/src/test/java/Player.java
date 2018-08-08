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
 * USE id1 id2 to use item id1 on creature id2.
 * USE id -1 to use item id.
 * PASS to do nothing this turn.
 * PICK
 * BCDGLW
 * Breakthrough: Creatures with Breakthrough can deal extra damage to the opponent when they attack enemy creatures.
 *      If their attack damage is greater than the defending creature's defense, the excess damage is dealt to the opponent.
 * Charge: Creatures with Charge can attack the turn they are summoned.
 * Guard: Enemy creatures must attack creatures with Guard first.
 */

/**
 * B -> breakthrough : 突破
 * C -> charge : 冲锋
 * D -> drain : 系命
 * G -> guard : 嘲讽
 * L -> lethal : 致命
 * w -> ward : 护盾
 */
class Keywords {

    boolean hasBreakthrough;     // 突破
    boolean hasCharge;           // 冲锋
    boolean hasDrain;            // 系命
    boolean hasGuard;            // 嘲讽
    boolean hasLethal;           // 致命
    boolean hasWard;             // 护盾

    public boolean hasAnyKeyword() {
        return hasBreakthrough || hasCharge || hasDrain || hasGuard || hasLethal || hasWard;
    }

    public Keywords(String data) {
        hasBreakthrough = data.charAt(0) == 'B';
        hasCharge = data.charAt(1) == 'C';
        hasDrain = data.charAt(2) == 'D';
        hasGuard = data.charAt(3) == 'G';
        hasLethal = data.charAt(4) == 'L';
        hasWard = data.charAt(5) == 'W';
    }

    public Keywords(Keywords keywords) {
        hasBreakthrough = keywords.hasBreakthrough;
        hasCharge = keywords.hasCharge;
        hasDrain = keywords.hasDrain;
        hasGuard = keywords.hasGuard;
        hasLethal = keywords.hasLethal;
        hasWard = keywords.hasWard;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(hasBreakthrough ? 'B' : '-');
        sb.append(hasCharge ? 'C' : '-');
        sb.append(hasDrain ? 'D' : '-');
        sb.append(hasGuard ? 'G' : '-');
        sb.append(hasLethal ? 'L' : '-');
        sb.append(hasWard ? 'W' : '-');
        return sb.toString();
    }

}

/**
 * 卡片信息
 */
class Card {
    int cardNumber;              // 卡片唯一编号
    int instanceId;              // 卡片实例id
    int location;                // 0 在手牌  1， 在场上 2. 在对面场上
    int cardType;                // 0: Creature  1: Green item 2: Red item 3: Blue item
    int cost;                    // 需要消耗的费用
    int attack;                  // 攻击力
    int defense;                 // 守备力
    Keywords keywords;
    int myHealthChange;          // 影响自己生命值
    int opponentHealthChange;    // 影响对方生命值
    int cardDraw;                // 影响下一轮抽卡

    String summom(){
        return "SUMMON " + this.instanceId;
    }

    String attack(Card otherCard){
        String result = "PASS";
        if (otherCard != null){
            result = "ATTACK " + this.instanceId + " " + otherCard.instanceId;
        }else{
            result = "ATTACK " + this.instanceId + " -1";
        }
        return result;
    }

    String attack(){
        return attack(null);
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
                ", abilities='" + keywords + '\'' +
                ", myHealthChange=" + myHealthChange +
                ", opponentHealthChange=" + opponentHealthChange +
                ", cardDraw=" + cardDraw +
                '}';
    }
}

/**
 * 玩家信息
 */
class PlayInfo {
    int health ;   // 生命力
    int mana ;     // 法力值
    int deck;      // 剩余卡牌
    int rune ;     // to be ignored in this league

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

/**
 * 游戏状态
 */
enum GameState {
    Draft, Battle
}

/**
 * 游戏西西里
 */
class GameInfo{
    GameState gameState = GameState.Draft;
    PlayInfo player = new PlayInfo();
    PlayInfo opponenter = new PlayInfo();
    List<Card> allCards = new ArrayList<>();
    List<Card> playerHandCreatureCards = new ArrayList<>();
    List<Card> playerHandItemCards = new ArrayList<>();
    List<Card> playerSideCards = new ArrayList<>();
    List<Card> opponentSideCards = new ArrayList<>();
    int opponentHand;
    int cardCount;

    @Override
    public String toString() {
        return "GameInfo{" +
                "gameState=" + gameState +
                ", player=" + player +
                ", opponenter=" + opponenter +
                ", allCards=" + allCards +
                ", playerHandCreatureCards=" + playerHandCreatureCards +
                ", playerSideCards=" + playerSideCards +
                ", opponentSideCards=" + opponentSideCards +
                ", opponentHand=" + opponentHand +
                ", cardCount=" + cardCount +
                '}';
    }
}

public class Player {

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
            gameInfo.gameState = gameInfo.player.mana == 0 ? GameState.Draft : GameState.Battle;

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
                card.keywords = new Keywords(in.next());
                card.myHealthChange = in.nextInt();
                card.opponentHealthChange = in.nextInt();
                card.cardDraw = in.nextInt();

                gameInfo.allCards.add(card);
                if (card.location == 0){
                    gameInfo.playerHandCreatureCards.add(card);
                }else if(card.location == 1){
                    if (card.cardType == 0){
                        gameInfo.playerHandCreatureCards.add(card);
                    }else {
                        gameInfo.playerHandItemCards.add(card);
                    }
                }else {
                    gameInfo.opponentSideCards.add(card);
                }
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            if (gameInfo.gameState == GameState.Draft){
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
        List<Card> guardCards = gameInfo.opponentSideCards.stream().filter(item -> item.keywords.hasGuard).collect(Collectors.toList());
        if (! guardCards.isEmpty()){
            gameInfo.opponentSideCards.removeAll(guardCards);
            guardCards = guardCards.stream().sorted(Comparator.comparingInt(o -> o.defense)).collect(Collectors.toList());
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
            long count = gameInfo.opponentSideCards.stream().filter(item -> item.keywords.hasGuard).count();
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
        do {
            int finalMana = gameInfo.player.mana;
            Optional<Card> any = gameInfo.playerHandCreatureCards.stream().filter(card -> card.cost <= finalMana).findAny();
            if (! any.isPresent()){
                return;
            }
            Card card = any.get();
            if (card.keywords.hasGuard){
                gameInfo.playerSideCards.add(card);
            }
            gameInfo.playerHandCreatureCards.remove(card);
            commandList.add(card.summom());
            gameInfo.player.mana = gameInfo.player.mana - any.get().cost;
        } while (gameInfo.player.mana > 0);
    }
}
