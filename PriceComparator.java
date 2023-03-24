/**
 * A price comparator for trade orders.
 * 
 * @author Edwin Li
 * @author William Li
 * @version 3/24/2023
 */
public class PriceComparator
    implements java.util.Comparator<TradeOrder>
{
    private boolean ascending;

    /**
     * Constructs a price comparator that compares two orders in ascending
     * order.
     */
    public PriceComparator()
    {
        ascending = true;
    }


    /**
     * Constructs a price comparator that compares two orders in ascending or
     * descending order. The order of comparison depends on the value of a given
     * parameter. Sets the private boolean ascending flag to asc.
     * 
     * @param asc comparison operator
     */
    public PriceComparator(boolean asc) // asc = ascending
    {
        ascending = asc;
    }


    /**
     * Compares two trade orders.
     * 
     * @param order1
     *            - the first order
     * @param order2
     *            - the second order
     * @return 0 if both orders are market orders; -1 if order1 is market and
     *         order2 is limit; 1 if order1 is limit and order2 is market; the
     *         difference in prices, rounded to the nearest cent, if both order1
     *         and order2 are limit orders. In the latter case, the difference
     *         returned is cents1 - cents2 or cents2 - cents1, depending on
     *         whether this is an ascending or descending comparator (ascending
     *         is true or false).
     */
    public int compare(TradeOrder order1, TradeOrder order2)
    {
        if (order1.isMarket() && order2.isMarket())
        {
            return 0;
        }

        else if (order1.isMarket() && order2.isLimit())
        {
            return -1;
        }

        else if (order1.isLimit() && order2.isMarket())
        {
            return 1;
        }

        else // if (order1.isLimit() && order2.isLimit())
        {
            if (ascending)
            {
                int x = (int)Math.round((order1.getPrice() * 100 
                - order2.getPrice() * 100));
                return x;
            }
            else
            {
                int x = (int)Math.round((order2.getPrice() * 100 - 
                order1.getPrice() * 100));
                return x;
            }
        }
    }
}
