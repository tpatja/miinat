package miinat.engine;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

/**
 * High score entry data class
 */
public class HighScoreEntry implements Externalizable {
    public MiinaEngine.Level level;
    public int time;
    public Date date;
    public String name;

    
    public HighScoreEntry() {
    }
    
    public HighScoreEntry(MiinaEngine.Level l, int time, Date date, String name) {
        this.level = l;
        this.time = time;
        this.date = (Date)date.clone();
        this.name = name;
    }
    
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBytes(this.toString());
        
    }

    @Override
    public void readExternal(ObjectInput in) 
            throws IOException, ClassNotFoundException {
        String data = "";
        int b;
        while( (b = in.read()) != -1) {
            data += (char)b;
        }
        this.fromString(data);
    }
    
    
    @Override
    public String toString() {
        String s = this.level.ordinal() + "," + this.time 
                + "," + this.date.getTime() + "," + this.name;
        return s;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o == null)
            return false;
        HighScoreEntry other = (HighScoreEntry)o;
        if(other == null)
            return false;
        return ( this.date.equals(other.date) &&
                this.time == other.time &&
                this.name.equals(other.name) &&
                this.level == other.level );
      
    }
    
    private void fromString(String data) {
        String tokens[] = data.split(",");
        this.level = MiinaEngine.Level.values()[Integer.parseInt(tokens[0])];
        this.time = Integer.parseInt(tokens[1]);
        this.date = new Date( Long.parseLong(tokens[2]) );
        this.name = tokens[3];
    }
    

}


    
  