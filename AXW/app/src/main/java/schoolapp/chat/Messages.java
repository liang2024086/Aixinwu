package schoolapp.chat;


import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import java.util.Date;
import java.util.Vector;

class Message{
    @Expose(serialize = false, deserialize = true)
    private Date time = new Date();
    @Expose(serialize = true, deserialize = true)
    private String content;
    @Expose(serialize = false, deserialize = true)
    private String from;
    @Expose(serialize = false, deserialize = true)
    private String to;
    public Date getTime(){
        return time;
    }
    public String getContent(){
        return content;
    }
    public int getFrom(){
        return Integer.parseInt(from);
    }
    public int getTo(){
        return Integer.parseInt(to);
    }

    Message(String _content){
        content = _content;
    }
}

public class Messages{

    private Vector<Message> messages = new Vector<>();
    Gson gson = new GsonBuilder().setExclusionStrategies(new CustomExclusionStrategy())
            .create();
    public void addMessage(String content){
        messages.add(new Message(content));
    }

    public String toString(){
        return gson.toJson(this);
    }

    public Vector<Message> getMessages(){
        return messages;
    }
    public Date getTime(int i){
        return messages.get(i).getTime();
    }
    public String getContent(int i){
        return messages.get(i).getContent();
    }
    public int getFrom(int i){
        return messages.get(i).getFrom();
    }
    public int getTo(int i){
        return messages.get(i).getTo();
    }
}

class CustomExclusionStrategy implements ExclusionStrategy {

    private Class classToExclude;


    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return false;
    }

    // This method is called for all classes. If the method returns false the
    // class is excluded.
    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        if (clazz.equals(Date.class))
            return true;
        if( clazz.equals(Gson.class))
            return true;
        return false;
    }

}