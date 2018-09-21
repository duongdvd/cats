package jp.co.willwave.aca.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConversionUtil {
    public static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<T>();
        if (obj != null) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
        }
        return result;
    }

    /*
    Use this method only when casting object like Path<?>
     */
    public static <T> T castObject(Object obj, Class<T> clazz) {
        if (obj != null) {
            return clazz.cast(obj);
        }
        return null;
    }

    public static <T> T  mapper(Object o, Class<T> clazz) {
        return new ModelMapper().map(o, clazz);
    }

    public static <T> T strictMapper(Object o, Class<T> clazz) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return mapper.map(o, clazz);
    }

    public static void mapper(Object source, Object destination) {
        new ModelMapper().map(source, destination);
    }

    public static <T> List<T> mapperAsList(List obj, Class<T> clazz) {
        List<T> result = new ArrayList<T>();
        if (obj != null) {
            for (Object o : (List<?>) obj) {
                result.add(mapper(o, clazz));
            }
        }
        return result;
    }

    public static <T> T clone(T o, Class<T> clazz) throws IOException {
        if (o == null) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String objectString = objectMapper.writeValueAsString(o);
        return objectMapper.readValue(objectString, clazz);
    }
}
