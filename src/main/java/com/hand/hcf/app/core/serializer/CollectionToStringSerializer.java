package com.hand.hcf.app.core.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/7/16 11:32
 * @remark 将容器中的数据转换为String类型
 */
@JacksonStdImpl
@SuppressWarnings("serial")
public class CollectionToStringSerializer extends ToStringSerializer {

    /**
     * Singleton instance to use.
     */
    public final static CollectionToStringSerializer instance = new CollectionToStringSerializer();

    /**
     *<p>
     * Note: usually you should NOT create new instances, but instead use
     * {@link #instance} which is stateless and fully thread-safe. However,
     * there are cases where constructor is needed; for example,
     * when using explicit serializer annotations like
     * {@link com.fasterxml.jackson.databind.annotation.JsonSerialize#using}.
     */
    public CollectionToStringSerializer() { super(Object.class); }

    /**
     * Sometimes it may actually make sense to retain actual handled type, so...
     *
     * @since 2.5
     */
    public CollectionToStringSerializer(Class<?> handledType) {
        super(handledType);
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        Object val;
        if(value.getClass().isArray()){
            List<String> objects = new ArrayList<>();
            for(Object o : (Object[]) value){
                if(o != null){
                    objects.add(o.toString());
                }else{
                    objects.add(null);
                }
            }
            val = objects;
        }else if(Collection.class.isAssignableFrom(value.getClass())){
            List<String> objects = new ArrayList<>();
            for(Object o : (Collection) value){
                if(o != null){
                    objects.add(o.toString());
                }else{
                    objects.add(null);
                }
            }
            val = objects;
        }else{
            val = value.toString();
        }
        gen.writeObject(val);
    }
}
