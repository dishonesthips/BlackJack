/* By Borna Houmani-Farahani
 * Blackjack OOP Game 
 *   
 * ICS4U
 * Ms. Strelkovska
 * 
 * 10/21/16
 */
import java.util.*;

public class HoumaniB_BlackJack 
{
	public static void main(String[] args) 
	{
		new Game();
	}
}

class Game
{
	public Game()
	{
		Scanner input = new Scanner(System.in);
		
		boolean newRound = true;
		
		boolean stand;
		String name, choice;
		
		//initializing objects
		Deck d1 = new Deck(1);
		Player [] players = new Player[2];
		
		System.out.println("What is your name?");
		name = input.nextLine().toUpperCase().trim();
		
		players[0] = new Dealer(14);
		
		//if they did not enter a name, use the default constructor
		if (name.equals(""))
			players[1] = new Player();
		else
			players[1] = new Player(name);
		
		System.out.println("Deck:");
		System.out.println(d1);
		
		System.out.println("Shuffled:");
		d1.shuffle(100);
		System.out.println(d1);
		
		while (newRound)
		{
			//resets players at the start of new round
			for (int i = 0; i < players.length; i++)
			{
				players[i].init();
			}
			
			System.out.println("-------------------------------------------------------------\n" + "STARTING SCENARIO:\n");
			
			//deal 2 cards to each player
			for (int i = 0; i < players.length; i++)
			{
				players[i].takeCard(d1.giveCard());
				players[i].takeCard(d1.giveCard());
				System.out.println(players[i].showHand(false)); //false to denote that dealer should only reveal 1
			}
			
			System.out.println();
			
			//check for natural blackjack in all players
			for (int i = 0; i < players.length; i++)
			{
				if (players[i].check21())
					System.out.println(players[i].getName() + " has a blackjack!");
			}
			
			//game operations for each player
			//goes backwards in order to start with the player rather than the dealer
			for (int i = players.length-1; i >= 0; i--)
			{
				//if round has not yet ended (players[0] meaning the dealer)
				if (!players[i].getHas21() && !players[0].getHas21())
				{
					System.out.println("-------------------------------------------------------------\n" 
							+ players[i].getName() + "'S TURN:");
					
					stand = false;
					while (stand == false && players[i].checkBust() == false)
					{
						System.out.println(players[i].showHand(true) + "\n");
						
						if (players[i].action(input))//calling the player to make an action (true -> hit, false -> stand)
						{
							System.out.println(players[i].getName() + " hits.");
							players[i].showTakeCard(d1.giveCard()); //prints process of taking card
							//check for 21 every time they take a card
							if (players[i].check21())
							{
								System.out.println(players[i].showHand(true) +"\n" );
								System.out.println(players[i].getName() + " has 21!");
								break;
							}
						}
						else //if they chose to stand
						{
							stand = true;
							System.out.println(players[i].getName() + " stands.");
						}
					}
					//if they busted, show final hand (and total) 
					if (players[i].checkBust())
					{
						System.out.println(players[i].showHand(true) + "\n");
						System.out.println(players[i].getName() + " busted.");	
					}
				}
			}
			System.out.println("-------------------------------------------------------------\n" +"ROUND SUMMARY:\n");
			//for each player starting from 1 (excludes dealer)
			//compare final total with dealer and print result showing victory, defeat, or tie.
			for (int i = 1; i < players.length; i++)
			{
				if (players[i].checkBust())
					System.out.println(players[i].getName() +" busted! Therefore, they have lost this round. Sorry!");
				else if (players[0].checkBust())
					System.out.println("The dealer busted! Therefore, you have won this round!");
				else
					System.out.println(players[i].compareEnd(players[0].getTotals()));
			}
			
			System.out.println("\nDeck at end  of round:");
			System.out.println(d1);
			
			System.out.println("\nEnter \"Y\" to play another round. Enter anything else to quit.");
			choice = input.nextLine().toUpperCase().trim();
			
			if (!choice.equals("Y"))
				newRound = false;
		}
		input.close();
	}
}

class Player
{
	private static int numPlayers = 0;
	protected int [] hand, totals;
	protected int numCards, numAces;
	protected String name;
	protected boolean has21;
	
	//constructors
	public Player()//default constructor
	{
		this("PLAYER "+ (numPlayers)); //gives numbered name
	}
	public Player(String name)
	{
		this.name = name;
		numPlayers++;
	}
	//initialization operations called at the start of every round
	public void init()
	{
		hand = new int[11];
		totals = new int[2];
		numCards = 0;
		has21 = false;
		numAces  = 0;
	}
	//get methods
	public String getName()
	{
		return name;
	}
	public int [] getTotals()
	{
		return totals;
	}
	public boolean getHas21()
	{
		return has21;
	}
	//returns statement that compares the player total to dealer total
	public String compareEnd(int [] dealerTotals)
	{
		String header, winLose, footer;
		int pTotal, dTotal;
		
		//finds player and dealer totals
		if (totals[1] <= 21 && totals[1] != 0)
			pTotal = totals[1];
		else
			pTotal = totals[0];
		
		if (dealerTotals[1] <= 21 && dealerTotals[1] != 0)
			dTotal = dealerTotals[1];
		else
			dTotal = dealerTotals[0];
		
		header = name + "'s total of " + pTotal;
		
		//compares totals and assigns comparing statement
		if (pTotal == dTotal)
			winLose = " ties against";
		else if (pTotal > dTotal)
			winLose = " wins against";
		else
			winLose = " loses against";
		
		footer = " the dealer's total of " + dTotal;
		
		return (header + winLose + footer + ".");
	}
	//adds given card to hand
	public void takeCard(int card)
	{
		hand[numCards++] = card;
		calcTotals();
	}
	public void showTakeCard(int card)//prints out what card was added
	{
		takeCard(card);
		System.out.println(name + " draws a " + Deck.cardToString(card));
	}
	//updates has21 and returns it
	public boolean check21()
	{
		if (totals[1] == 21 || totals[0] == 21)
			has21 = true;
		return has21;
	}
	public boolean checkBust()
	{
		if (numAces > 0 )
		{ 
			if (totals[0] > 21 && totals[1] > 21)
				return true;
			else
				return false;
		}
		else
			if (totals[0] > 21)
				return true;
		return false;
	}
	public void calcTotals()
	{
		int currentCard, total = 0;
		numAces = 0;
		
		for (int i = 0; i < numCards; i++)
		{
			currentCard = Deck.cardToValue(hand[i]); 
			if (currentCard == -1)
				numAces ++;
			else
				total += currentCard;
		}

		totals[0] = total + numAces * 1;
		totals[1] = 0;
			
		if (numAces > 0)
		{
			totals[1] = total + 11 + (numAces-1)*1;
		}
	}
	//boolean is used when player is computer controlled
	public String showHand(boolean reveal)
	{
		return showHand();
	}
	//returns string that displays hand and totals beneath it
	public String showHand()
	{
		String result = "\n" + name + "'s hand:\n";
		
		for (int i = 0; i < numCards; i++)
		{
			result += Deck.cardToString(hand[i]) + " ";
		}
		result += "\n";
		
		if (numAces > 0 && totals[1] <= 21)
		{
			
			result += name + "'s totals are: " + totals[0] + " and " + totals[1];
		}
		else
			result += name + "'s total is: " + totals[0];
		return result;
	}
	//take in scanner to ask the player if they want to hit or not
	public boolean action(Scanner input)
	{
		String enter;
		System.out.println("Enter \"H\" to hit. Anything else to stand:");
		enter = input.nextLine().toUpperCase().trim();
		
		if (enter.equals("H"))
			return true;
		return false;
	}
}


class Dealer extends Player 
{
	private static int limit;
	
	public Dealer(int limit)
	{
		super("DEALER");
		this.limit = limit;
	}
	
	//returns dealer's entire hand and total or just the first card depending on the boolean argument
	public String showHand(boolean reveal)
	{
		if (reveal)
			return showHand();
		else
		{
			String result = name +"'s hand:\n" + Deck.cardToString(hand[0]) + " ??";
			return result;
		}
	}
	//instead of using the scanner, call computer decision making method below
	public boolean action(Scanner input)
	{
		return action();
	}
	//decides to hit or not based on the established limit
	public boolean action()
	{
		if (numAces > 0 && totals[1] < 21 && totals[1] <= limit)
			return true;
		else if (totals[0] <= limit)
			return true;
		return false;
		
	}
}

class Deck
{
	private int [] cards;
	private int cardsTaken;
	
	//constructor
	public Deck(int numDecks) 
	{
		cards = new int[52*numDecks];
		cardsTaken = 0;
		
		for (int i =  0; i < numDecks; i++)
			for (int j = 0; j < 52; j++)
				cards[j + (i*52)] = j;
	}
	//returns first card, moves pointer to next card
	//if the deck is less than 10 cards, refill it 
	public int giveCard()
	{
		int card = cards[cardsTaken++];
		
		if (cardsTaken > 42)
		{
			refillDeck(100);
			System.out.println("\n(REFILLING DECK)\n");
		}
		
		return card;
	}
	//method used when testing to rig deck
	public void replace(int val, int index)
	{
		cards[index]= val;
	}
	//static methods converting card indentifiers to String and value
	public static String cardToString(int card)
	{
		int suit = card % 4 + 3;
		int numVal = card/4 + 1;
		String strVal;
		if (numVal ==  1)
			strVal = "A";
		else if (numVal==11)
			strVal = "J";
		else if (numVal==12)
			strVal = "Q";
		else if (numVal==13)
			strVal = "K";
		else
			strVal = ""+numVal;
		
		return strVal+ (char)suit;
	}
	public static int cardToValue(int card)
	{
		int numVal =  card/4 + 1;
		//if ace, return -1 because it is special and can be 1 or 11
		if (numVal == 1) 
			return -1;
		else if (numVal > 10)
			numVal = 10;
		
		return numVal;
	}
	//shuffle method
	public void shuffle(int shuffle)
	{
		int temp;
		int rand1, rand2;
		for (int i = 0; i < shuffle; i++)
		{
			rand1 = (int)(Math.random() * cards.length);
			rand2 = (int)(Math.random() * cards.length);
			
			temp = cards[rand1];
			cards[rand1] = cards[rand2];
			cards[rand2] = temp;
		}
	}
	//simulates switching the deck
	public void refillDeck(int shuffle)
	{
		cardsTaken = 0;
		shuffle(shuffle);
	}
	//prints deck in order
	public String toString()
	{
		String result = "";
		
		for (int i = cardsTaken; i < cards.length; i++)
		{
			result += cardToString(cards[i]) + " ";
		}
		return result;
	}
}