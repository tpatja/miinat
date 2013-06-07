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
        this.date = new Date();
    }
    
    public HighScoreEntry(MiinaEngine.Level l, int time, Date date, String name) {
        this.level = l;
        this.time = time;
        this.date = date;
        this.name = name;
    }
    
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBytes(this.rot13(this.toString()));
        
    }

    @Override
    public void readExternal(ObjectInput in) 
            throws IOException, ClassNotFoundException {
        String data = "";
        int b;
        while( (b = in.read()) != -1) {
            data += (char)b;
        }
        //System.out.println("readExternal, plain data=" + data);
        //System.out.println("rotted=" + this.rot13(data));
        this.fromString(this.rot13(data));
    }
    
    
    @Override
    public String toString() {
        String s = this.level.ordinal() + "," + this.time 
                + "," + this.date.getTime() + "," + this.name;
        //System.out.println(s);
        return s;
    }
    
    private void fromString(String data) {
        String tokens[] = data.split(",");
        
        for(String token : tokens) {
            System.out.println("token " + token);
        }
        
        this.level = MiinaEngine.Level.values()[Integer.parseInt(tokens[0])];
        this.time = Integer.parseInt(tokens[1]);
        this.date = new Date( Long.parseLong(tokens[2]) );
        this.name = tokens[3];
    }
    
    public static String rot13(String input) {
        if(input == null)
            return input;
        String ret = "";
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if       (c >= 'a' && c <= 'm') c += 13;
            else if  (c >= 'A' && c <= 'M') c += 13;
            else if  (c >= 'n' && c <= 'z') c -= 13;
            else if  (c >= 'N' && c <= 'Z') c -= 13;
            ret += c;
        }
        return ret;
    }

}


    
  