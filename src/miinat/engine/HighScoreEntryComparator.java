
package miinat.engine;

import java.util.Comparator;

/**
 *
 * Comparator needed for sorting high score entries.
 * Sorts entries by time (ascending)
 * 
 */
public class HighScoreEntryComparator implements Comparator<HighScoreEntry> {

    @Override
    public int compare(HighScoreEntry o1, HighScoreEntry o2) {
          int time1=o1.time;
          int time2=o2.time;

          if(time1>time2)
              return +1;
          else if(time1<time2)
              return -1;
          else
              return 0;
    }
}
