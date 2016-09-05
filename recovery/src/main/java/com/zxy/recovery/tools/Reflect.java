package com.zxy.recovery.tools;

import android.text.TextUtils;

import com.zxy.recovery.exception.ReflectException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by zhengxiaoyong on 16/8/26.
 */
public class Reflect {

    private Class<?> mReflectObjectClazz;

    private Reflect(String className) {
        checkNotEmpty(className, "ClassName can not be empty!");
        try {
            this.mReflectObjectClazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ReflectException("Class:" + className + " can not be found!", e.getCause());
        }
    }

    private Reflect(Class<?> clazz) {
        checkNotNull(clazz, "Reflect class can not be null!");
        this.mReflectObjectClazz = clazz;
    }

    public static Reflect on(String className) {
        return new Reflect(className);
    }

    public static Reflect on(Class<?> clazz) {
        return new Reflect(clazz);
    }

    public final ReflectMethod method(String methodName, Class<?>... types) {
        checkNotEmpty(methodName, "MethodName can not be empty!");
        Method method;
        try {
            if (types == null || types.length == 0) {
                method = mReflectObjectClazz.getDeclaredMethod(methodName);
            } else {
                method = mReflectObjectClazz.getDeclaredMethod(methodName, types);
            }
            method.setAccessible(true);
            return ReflectMethod.create(method);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return ReflectMethod.create(null);
    }

    public static final class ReflectMethod {
        private Method method;

        private ReflectMethod(Method method) {
            this.method = method;
        }

        static ReflectMethod create(Method method) {
            return new ReflectMethod(method);
        }

        public Method obtain() {
            return method;
        }

        public Object invoke(Object invoker, Object... params) {
            if (method == null)
                return null;
            if (invoker == null) {
                if ((method.getModifiers() & Modifier.STATIC) == 0)
                    throw new ReflectException("Invoker can not be null!");
            }
            try {
                if (params == null || params.length == 0)
                    return method.invoke(invoker);
                return method.invoke(invoker, params);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public final ReflectField field(String fieldName) {
        checkNotEmpty(fieldName, "FieldName can not be empty!");
        try {
            Field field = mReflectObjectClazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return ReflectField.create(field);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return ReflectField.create(null);
    }

    public static final class ReflectField {
        private Field field;

        private ReflectField(Field field) {
            this.field = field;
        }

        static ReflectField create(Field field) {
            return new ReflectField(field);
        }

        public Field obtain() {
            return field;
        }

        public void set(Object target, Object value) {
            if (field == null)
                return;
            if (target == null) {
                if ((field.getModifiers() & Modifier.STATIC) == 0)
                    throw new ReflectException("Target object can not be null!");
            }
            try {
                field.set(target, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        public Object get(Object target) {
            if (field == null)
                return null;
            if (target == null) {
                if ((field.getModifiers() & Modifier.STATIC) == 0)
                    throw new ReflectException("Target object can not be null!");
            }
            try {
                return field.get(target);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public final ReflectConstructor constructor(Class<?>... types) {
        Constructor constructor;
        try {
            if (types == null || types.length == 0) {
                constructor = mReflectObjectClazz.getDeclaredConstructor();
            } else {
                constructor = mReflectObjectClazz.getDeclaredConstructor(types);
            }
            constructor.setAccessible(true);
            return ReflectConstructor.create(constructor);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return ReflectConstructor.create(null);
    }

    public static final class ReflectConstructor {
        private Constructor constructor;

        private ReflectConstructor(Constructor constructor) {
            this.constructor = constructor;
        }

        static ReflectConstructor create(Constructor constructor) {
            return new ReflectConstructor(constructor);
        }

        public Constructor obtain() {
            return constructor;
        }

        public Object newInstance(Object... params) {
            if (constructor == null)
                return null;
            try {
                if (params == null || params.length == 0)
                    return constructor.newInstance();
                return constructor.newInstance(params);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public Object newInstance() {
        if (mReflectObjectClazz == null)
            return null;
        try {
            return mReflectObjectClazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static <T> void checkNotNull(T t, String message) {
        if (t == null)
            throw new ReflectException(String.valueOf(message));
    }

    private static void checkNotEmpty(String t, String message) {
        if (TextUtils.isEmpty(t))
            throw new ReflectException(String.valueOf(message));
    }

}
