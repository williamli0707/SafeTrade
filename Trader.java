import java.lang.reflect.*;
import java.util.*;

/**
 * Represents a stock trader.
 */
public class Trader implements Comparable<Trader>
{
    private Brokerage brokerage;
    private String screenName, password;
    private TraderWindow myWindow;
    private Queue<String> mailbox;

    /**
     * Constructs a new trader, affiliated with a given brokerage, with a given
     * screen name and password.
     *
     * @param brokerage the brokerage for this trader.
     * @param name user name.
     * @param pswd password.
     */
    public Trader( Brokerage brokerage, String name, String pswd )
    {
        this.brokerage = brokerage;
        screenName = name;
        password = pswd;
        mailbox = new LinkedList<String>();
    }

    /**
     * Returns the screen name for this trader.
     *
     * @return the screen name for this trader.
     */
    public String getName()
    {
        return screenName;
    }

    /**
     * Returns the password for this trader.
     *
     * @return the password for this trader.
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Compares this trader to another by comparing their screen names case
     * blind.
     *
     * @param other the reference to a trader with which to compare.
     * @return the result of the comparison of this trader and
     *         <code>other</code>.
     */
    public int compareTo( Trader other )
    {
        return screenName.compareToIgnoreCase( other.getName() );
    }

    /**
     * Indicates whether some other trader is "equal to" this one, based on
     * comparing their screen names case blind. This method will throw a
     * <code>ClassCastException</code> if other is not an instance of
     * <code>Trader</code>.
     *
     * @param other the reference to an object with which to compare.
     * @return true if this trader's screen name is the same as
     *         <code>other</code>'s; false otherwise.
     */
    public boolean equals( Object other )
    {
        // use instanceof to avoid potential ClassCastException as indicated
        // in header comment. Note - not part of AP subset
        if ( other == null || !( other instanceof Trader ) )
        {
            return false;
        }

        // return screenName.equalsIgnoreCase(((Trader)other).getName());
        return compareTo( (Trader)other ) == 0;
    }

    /**
     * Creates a new <code>TraderWindow</code> for this trader and saves a
     * reference to it in <code>myWindow</code>. Removes and displays all the
     * messages, if any, from this trader's mailbox by calling
     * <code>myWindow.showMessage(msg)</code> for each message.
     */
    public void openWindow()
    {
        myWindow = new TraderWindow( this );
        while ( !mailbox.isEmpty() )
        {
            myWindow.showMessage( mailbox.remove() );
        }
    }

    /**
     * Returns true if this trader has any messages in its mailbox.
     *
     * @return true if this trader has messages; false otherwise.
     */
    public boolean hasMessages()
    {
        return !mailbox.isEmpty();
    }

    /**
     * Adds <code>msg</code> to this trader's mailbox and displays all messages.
     * If this trader is logged in (<code>myWindow</code> is not
     * <code>null</code>) removes and shows all the messages in the mailbox by
     * calling <code>myWindow.showMessage(msg)</code> for each <code>msg</code>
     * in the mailbox.
     *
     * @param msg a message to be added to this trader's mailbox.
     */
    public void receiveMessage( String msg )
    {
        mailbox.add( msg );

        if ( myWindow != null )
        {
            while ( !mailbox.isEmpty() )
            {
                myWindow.showMessage( mailbox.remove() );
            }
        }
    }

    /**
     * Requests a quote for a given stock symbol from the brokerage by calling
     * brokerage's <code>getQuote</code>.
     *
     * @param symbol a stock symbol for which a quote is requested.
     */
    public void getQuote( String symbol )
    {
        brokerage.getQuote( symbol, this );
    }

    /**
     * Places a given order with the brokerage by calling brokerage's
     * <code>placeOrder</code>.
     *
     * @param order a trading order to be placed.
     */
    public void placeOrder( TradeOrder order )
    {
        brokerage.placeOrder( order );
    }

    /**
     * Logs out this trader. Calls <code>brokerage</code>'s <code>logout</code>
     * for this trader. Sets <code>myWindow</code> to <code>null</code> (this
     * method is called from a <code>TraderWindow</code>'s window listener when
     * the "close window" button is clicked).
     */
    public void quit()
    {
        myWindow = null;
        brokerage.logout( this );
    }

    //
    // The following are for test purposes only
    //
    protected Queue<String> mailbox()
    {
        return mailbox;
    }

    /**
     * Intended only for debugging.
     *
     * <p>
     * A generic toString implementation that uses reflection to print names and
     * values of all fields <em>declared in this class</em>. Note that
     * superclass fields are left out of this implementation.
     * </p>
     *
     * @return a string representation of this Trader.
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
                if ( field.getType().getName().equals( "Brokerage" ) )
                    str += separator + field.getType().getName() + " "
                            + field.getName();
                else
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

    public static void main( String[] args )
    {
        Trader joe = new Trader( null, "Joe", "pwd" );
        System.out.println( joe );
    }

}
