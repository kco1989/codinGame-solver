import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public boolean hasSomeKeyword(Keywords others){
        return hasBreakthrough == others.hasBreakthrough ||
                hasCharge == others.hasCharge ||
                hasDrain == others.hasDrain ||
                hasGuard == others.hasGuard ||
                hasLethal == others.hasLethal ||
                hasWard == others.hasWard;
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

    int remainMana;             // 如果被使用,玩家剩余的法力值

    boolean isCreature(){
        return cardType == 0;
    }
    boolean isGreenItem(){
        return cardType == 1;
    }
    boolean isRedItem(){
        return cardType == 2;
    }
    boolean isBlueItem(){
        return cardType == 3;
    }
    /**
     * 召唤
     */
    String toSummom(){
        return String.format("SUMMON %d", this.instanceId);
    }

    /**
     * 攻击
     */
    String toAttack(Card otherCard){
        return otherCard != null ?
                String.format("USE %d %d", this.instanceId, otherCard.instanceId) :
                String.format("USE %d %d", this.instanceId, -1);
    }

    /**
     * 攻击
     */
    String toAttack(){
        return toAttack(null);
    }

    /**
     * 使用
     */
    String toUse(Card otherCard){
        return otherCard != null ?
                String.format("ATTACK %d %d", this.instanceId, otherCard.instanceId) :
                String.format("ATTACK %d %d", this.instanceId, -1);
    }

    /**
     * 使用
     */
    String toUse(){
        return toUse(null);
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
    List<String> commandList = new ArrayList<>();
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

            if (isOneTurnKill(gameInfo)){
                System.out.println(String.join(";", gameInfo.commandList));
                continue;
            }

            // 召唤
            doSummonOrUse(gameInfo);

            doAttack(gameInfo);
            System.out.println(gameInfo.commandList.isEmpty() ? "PASS" : String.join(";", gameInfo.commandList));
        }
    }

    /**
     * 攻击阶段
     */
    private static void doAttack(GameInfo gameInfo) {
        // 自己场上没有怪兽， 则跳过攻击阶段
        if (gameInfo.playerSideCards.isEmpty()){
            return;
        }
        // 对面场上没有怪兽，直接攻击
        if (gameInfo.opponentSideCards.isEmpty()){
            gameInfo.playerSideCards.stream().forEach(item -> gameInfo.commandList.add(item.toAttack()));
            return;
        }

        // 处理攻击嘲讽怪
        List<Card> guardCards = gameInfo.opponentSideCards.stream().filter(item -> item.keywords.hasGuard).collect(Collectors.toList());
        if (! guardCards.isEmpty()){
            gameInfo.opponentSideCards.removeAll(guardCards);
            guardCards = guardCards.stream().sorted(Comparator.comparingInt(o -> o.defense)).collect(Collectors.toList());
            attackGuard(gameInfo.commandList, gameInfo, guardCards);
            // 场上没有怪兽了，则返回
            if (gameInfo.playerSideCards.isEmpty()){
                return;
            }
            // 场上的还能再攻击的怪兽能打死对方
            if (isOneTurnKill(gameInfo)){
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
                gameInfo.commandList.add(first.get().toAttack(card));
                gameInfo.playerSideCards.remove(first.get());
            }
        }

        // 还有未行动的怪兽
        if (! gameInfo.playerSideCards.isEmpty()){
            gameInfo.playerSideCards.stream().forEach(item -> gameInfo.commandList.add(item.toAttack()));
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
            commandList.add(attack.toAttack(defense));
            if (attack.attack >= defense.defense){
                guardCards.remove(defense);

            }
            // 不能再攻击了
            gameInfo.playerSideCards.remove(attack);
        }while (!(guardCards.isEmpty() || gameInfo.playerSideCards.isEmpty()));
    }

    /**
     * 判断能否一回杀
     */
    private static boolean isOneTurnKill(GameInfo gameInfo) {
        // 手牌和场上都没有怪直接返回
        if (gameInfo.playerSideCards.isEmpty() && gameInfo.playerHandItemCards.isEmpty()){
            return false;
        }

        // 对方场上嘲讽怪的数量
        long guardCount = gameInfo.opponentSideCards.stream().filter(item -> item.keywords.hasGuard).count();
        if (guardCount > 0){
            return false;       // 如果有嘲讽怪,这暂时不处理,先处理对面场上的嘲讽怪先
        }else{
            return isOneTurnKillWithoutGuard(gameInfo);
        }
    }

    /**
     * 在对方在没有拥有嘲讽怪的情况下
     * 判断时候能一回杀
     */
    private static boolean isOneTurnKillWithoutGuard(GameInfo gameInfo) {
        // 计算场上总攻击力
        int sumAttack = gameInfo.playerSideCards.stream().map(item -> item.attack).reduce(0, (sum, item) -> sum + item);
        if (sumAttack >= gameInfo.opponenter.health){
            gameInfo.playerSideCards.stream().forEach(item -> gameInfo.commandList.add(item.toAttack()));
            return true;
        }

        // 过滤本回合可以直接攻击或者能造成伤害的怪
        Stream<Card> creatureStream = gameInfo.playerHandCreatureCards.stream()
                // 过滤费用小于玩家法力值,且可以在本回合造成伤害的卡
                .filter(item -> gameInfo.player.mana >= item.cost && (item.keywords.hasCharge || item.opponentHealthChange < 0));

        // 过滤本回合可以造成伤害的蓝卡
        Stream<Card> blueStream = gameInfo.playerHandItemCards.stream()
                // 过滤费用小于玩家法力值且蓝卡
                .filter(item -> item.isBlueItem() && gameInfo.player.mana >= item.cost);

        // 按照伤害值倒序排序
        final int[] playMana = {gameInfo.player.mana};
        List<Card> collectCard = Stream.concat(creatureStream, blueStream)
                .sorted((o1, o2) -> getBlueItemOrChargeHurt(o2) - getBlueItemOrChargeHurt(o1))
                // 计算如果被使用,玩家还会剩余的法力值
                .map(item -> {
                    item.remainMana = playMana[0] - item.cost;
                    playMana[0] = item.remainMana;
                    return item;
                })
                .filter(item -> item.remainMana >= 0)
                .collect(Collectors.toList());

        Integer sumHurt = collectCard.stream()
                .map(Player::getBlueItemOrChargeHurt)
                .reduce(0, (sum, item) -> sum += item);

        if (sumHurt + sumAttack >= gameInfo.opponenter.health) {
            collectCard.stream().forEach(item -> {
                        if (item.isBlueItem()) {
                            gameInfo.commandList.add(item.toUse());
                        } else if (item.keywords.hasCharge) {
                            gameInfo.commandList.add(item.toSummom());
                            gameInfo.commandList.add(item.toAttack());
                        } else {
                            gameInfo.commandList.add(item.toSummom());
                        }
                    });
            gameInfo.playerSideCards.stream().forEach(item -> gameInfo.commandList.add(item.toAttack()));
            return true;
        }
        return false;
    }

    static int getBlueItemOrChargeHurt(Card card){
        int hurt;
        if (card.isBlueItem()) {
            hurt = Math.abs(card.defense + card.opponentHealthChange);
        } else if (card.isCreature()){
            hurt = (card.keywords.hasCharge ? card.attack : 0) + Math.abs(card.opponentHealthChange);
        }else {
            hurt = 0;
        }
        return hurt;
    }
    /**
     * 召唤怪兽
     */
    private static void doSummonOrUse(GameInfo gameInfo) {

        int handCardCount = gameInfo.playerHandItemCards.size() + gameInfo.playerHandCreatureCards.size();
        long canUserCreatureCount = gameInfo.playerHandCreatureCards.stream()
                .filter(item -> item.cost <= gameInfo.player.mana)
                .count();
        long canUserItemCount = gameInfo.playerHandItemCards.stream()
                .filter(item -> item.cost <= gameInfo.player.mana)
                .count();

        // 没有可以使用的牌
        if (canUserCreatureCount + canUserItemCount == 0){
            return;
        }

        // 如果对面场上没有怪,判断如何使用绿卡
        // 如果对面场上有怪,判断如何使用红卡或者蓝卡
        // 最后尽可能的召唤怪兽
        if (gameInfo.opponentSideCards.isEmpty()){
            toUserGreenItem(gameInfo);
        }else {
            toUserRedOrBlueItem(gameInfo);
        }
        asFarAsPossibleToSummon(gameInfo);
    }

    private static void toUserRedOrBlueItem(GameInfo gameInfo) {
        List<Card> redOrBlueCard = gameInfo.playerHandItemCards.stream()
                .filter(item -> (item.isBlueItem() || item.isRedItem()) && item.cost <= gameInfo.player.mana)
                .collect(Collectors.toList());
        if (redOrBlueCard.isEmpty()){
            return;
        }
        // todo4lvsw how to use red or blue card
    }

    private static void toUserGreenItem(GameInfo gameInfo) {
        List<Card> greenCard = gameInfo.playerHandItemCards.stream()
                .filter(item -> item.isGreenItem() && item.cost <= gameInfo.player.mana)
                .collect(Collectors.toList());
        if (greenCard.isEmpty() || gameInfo.playerSideCards.isEmpty()){
            return;
        }
        // todo4lvsw how to use green card
    }

    private static void asFarAsPossibleToSummon(GameInfo gameInfo) {
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
            gameInfo.commandList.add(card.toSummom());
            gameInfo.player.mana = gameInfo.player.mana - any.get().cost;
        } while (gameInfo.player.mana > 0);
    }
}
