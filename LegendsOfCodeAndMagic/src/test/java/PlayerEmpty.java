import java.util.*;
import java.util.stream.Collectors;


/**
 * AI description
 * Draft phase:
 * - always pick the first card
 * Game phase:
 * - do nothing (outputs single ';')
 * SUMMON id to summon the creature id from your hand.
 * ATTACK id1 id2 to toAttack creature id2 with creature id1.
 * ATTACK id -1 to toAttack the opponent directly with creature id.
 * PASS to do nothing this turn.
 * PICK
 */
public class PlayerEmpty {
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        GameState playState = GameState.Draft;
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
            playState = player.mana == 0 ? GameState.Draft : GameState.Battle;
            System.err.println("gameState -> " + playState);
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
            if (playState == GameState.Draft){
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
                    list.add(any.get().toSummom());
                    mana = mana - any.get().cost;
                } else {
                    break;
                }
            } while (mana > 0);

            // 计算场上自己场上怪攻击之和
            if (!playerSideCards.isEmpty()){
                int sumAttack = playerSideCards.stream().map(item -> item.attack).reduce(0, (sum, item) -> sum + item);
                if (sumAttack >= opponent.health || opponentSideCards.isEmpty()){
                    playerSideCards.stream().forEach(item -> list.add(item.toAttack()));
                    System.out.println(String.join(";", list));
                    continue;
                }
            }

            // 对面场上有怪
            if (!opponentSideCards.isEmpty()){
                opponentSideCards = opponentSideCards.stream().sorted((o1, o2) -> o1.defense - o2.defense).collect(Collectors.toList());
                for (Card card : opponentSideCards){
                    Optional<Card> first = playerSideCards.stream().filter(item -> item.attack >= card.defense && item.defense > item.attack).findFirst();
                    if (first.isPresent()){
                        list.add(first.get().toAttack(card));
                        playerSideCards.remove(first.get());
                    }
                }
            }

            // 还有未行动的怪兽
            if (! playerSideCards.isEmpty()){
                playerHandCards.stream().forEach(item -> list.add(item.toAttack()));
            }

            System.out.println(String.join(";", list));

        }
    }
}
