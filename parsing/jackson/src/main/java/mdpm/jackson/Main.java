package mdpm.jackson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

@JsonTypeInfo(include=As.WRAPPER_OBJECT, use=Id.CLASS) 
public class Main {

	static final ObjectMapper mapper = new ObjectMapper();

    private /*final*/ String id;
	private /*final*/ Info info;
	
	class Info {
	    public Info() {}
	    
	    Map<String,String> m = new HashMap<String, String>();
	}

    public static void main(String[] args) throws JsonProcessingException {
        Main m = new Main();
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        //mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
        //mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        mapper.configure(DeserializationFeature.WRAP_EXCEPTIONS, true);

        m.id = "asdf";
        m.info = m.new Info();
        m.info.m.put("k1", "v1");
        m.info.m.put("k2", "v2");

        ObjectWriter w = mapper.writer().withDefaultPrettyPrinter();
        System.out.println(w.writeValueAsString(m));
       
        try {
            final ObjectReader r = mapper.readerFor(Main.class);
            Main value = r.readValue(w.writeValueAsString(m));
            System.out.println(w.writeValueAsString(value));
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        

//        StringBuffer res = new StringBuffer("{\n  \"com.bearingpoint.abacus.messaging.message.ConfigurationChangedEventMessage\": ");
//        res.append(w.writeValueAsString(m));
//		res.append(" }");
//		
//		System.out.println(res.toString());
    }

}
