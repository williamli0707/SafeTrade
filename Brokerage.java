import java.lang.reflect.*;
import java.util.*;
//dfghighkfdjhg
/**
 * Represents a brokerage.
 */
public class Brokerage implements Login
{
    private Map<String, Trader> traders;
    private Set<Trader> loggedTraders;
    private StockExchange exchange;

    public Brokerage(StockExchange exchange) {
        this.exchange = exchange;
        loggedTraders = new TreeSet<>();
        traders = new TreeMap<>();
    }

    public int addUser(String name, String password) {
        int l1 = name.length(), l2 = password.length();
        if(l1 < 4 || l1 > 10) return -1;
        if(l2 < 2 || l2 > 10) return -2;
        if(traders.containsKey(name)) return -3;
        traders.put(name, new Trader(this, name, password));
        return 0;
    }

    public void getQuote(String symbol, Trader trader) {
        trader.recieveMessage(exchange.getQuote(symbol));
    }

    public int login(String name, String password) {
        if(!traders.containsKey(name)) return -1;
        if(!traders.get(name).getPassword().equals(password)) return -2;
        Trader trader = traders.get(name);
        if(loggedTraders.contains(trader)) return -3;
        loggedTraders.add(trader);
        trader.openWindow();
        trader.recieveMessage("Welcome to SafeTrade!");
        return 0;
    }

    public void logout(Trader trader) {
        loggedTraders.remove(trader);
    }

    public void placeOrder(TradeOrder order) {
        exchange.placeOrder(order);
    }

    //
    // The following are for test purposes only
    //
    protected Map<String, Trader> getTraders()
    {
        return traders;
    }

    protected Set<Trader> getLoggedTraders()
    {
        return loggedTraders;
    }

    protected StockExchange getExchange()
    {
        return exchange;
    }

    /**
     * <p>
     * A generic toString implementation that uses reflection to print names and
     * values of all fields <em>declared in this class</em>. Note that
     * superclass fields are left out of this implementation.
     * </p>
     * 
     * @return a string representation of this Brokerage.
     */
    public String toString()
    {
        String str = this.getClass().getName() + "[";
        String separator = "";

        Field[] fields = this.getClass().getDeclaredFields();

        for ( Field field : fields )
        {
            try
            {
                str += separator + field.getType().getName() + " "
                    + field.getName() + ":" + field.get( this );
            }
            catch ( IllegalAccessException ex )
            {
                System.out.println( ex );
            }

            separator = ", ";
        }

        return str + "]";
    }
}
