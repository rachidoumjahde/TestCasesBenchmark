package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.LRUMap;
import java.io.Serializable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TypeFactory implements Serializable {
    protected static final SimpleType CORE_TYPE_BOOL = new SimpleType(Boolean.TYPE);
    protected static final SimpleType CORE_TYPE_INT = new SimpleType(Integer.TYPE);
    protected static final SimpleType CORE_TYPE_LONG = new SimpleType(Long.TYPE);
    protected static final SimpleType CORE_TYPE_STRING = new SimpleType(String.class);
    private static final JavaType[] NO_TYPES = new JavaType[0];
    protected static final TypeFactory instance = new TypeFactory();
    private static final long serialVersionUID = 1;
    protected transient HierarchicType _cachedArrayListType;
    protected transient HierarchicType _cachedHashMapType;
    protected final TypeModifier[] _modifiers;
    protected final TypeParser _parser;
    protected final LRUMap<ClassKey, JavaType> _typeCache;

    private TypeFactory() {
        this._typeCache = new LRUMap<>(16, 100);
        this._parser = new TypeParser(this);
        this._modifiers = null;
    }

    protected TypeFactory(TypeParser typeParser, TypeModifier[] typeModifierArr) {
        this._typeCache = new LRUMap<>(16, 100);
        this._parser = typeParser;
        this._modifiers = typeModifierArr;
    }

    public TypeFactory withModifier(TypeModifier typeModifier) {
        if (this._modifiers != null) {
            return new TypeFactory(this._parser, (TypeModifier[]) ArrayBuilders.insertInListNoDup(this._modifiers, typeModifier));
        }
        return new TypeFactory(this._parser, new TypeModifier[]{typeModifier});
    }

    public static TypeFactory defaultInstance() {
        return instance;
    }

    public static JavaType unknownType() {
        return defaultInstance()._unknownType();
    }

    public static Class<?> rawClass(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        }
        return defaultInstance().constructType(type).getRawClass();
    }

    public JavaType constructSpecializedType(JavaType javaType, Class<?> cls) {
        if (!(javaType instanceof SimpleType) || (!cls.isArray() && !Map.class.isAssignableFrom(cls) && !Collection.class.isAssignableFrom(cls))) {
            return javaType.narrowBy(cls);
        }
        if (!javaType.getRawClass().isAssignableFrom(cls)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Class ");
            sb.append(cls.getClass().getName());
            sb.append(" not subtype of ");
            sb.append(javaType);
            throw new IllegalArgumentException(sb.toString());
        }
        JavaType _fromClass = _fromClass(cls, new TypeBindings(this, javaType.getRawClass()));
        Object valueHandler = javaType.getValueHandler();
        if (valueHandler != null) {
            _fromClass = _fromClass.withValueHandler(valueHandler);
        }
        Object typeHandler = javaType.getTypeHandler();
        if (typeHandler != null) {
            _fromClass = _fromClass.withTypeHandler(typeHandler);
        }
        return _fromClass;
    }

    public JavaType constructFromCanonical(String str) throws IllegalArgumentException {
        return this._parser.parse(str);
    }

    public JavaType[] findTypeParameters(JavaType javaType, Class<?> cls) {
        Class<?> rawClass = javaType.getRawClass();
        if (rawClass != cls) {
            return findTypeParameters(rawClass, cls, new TypeBindings(this, javaType));
        }
        int containedTypeCount = javaType.containedTypeCount();
        if (containedTypeCount == 0) {
            return null;
        }
        JavaType[] javaTypeArr = new JavaType[containedTypeCount];
        for (int i = 0; i < containedTypeCount; i++) {
            javaTypeArr[i] = javaType.containedType(i);
        }
        return javaTypeArr;
    }

    public JavaType[] findTypeParameters(Class<?> cls, Class<?> cls2) {
        return findTypeParameters(cls, cls2, new TypeBindings(this, cls));
    }

    public JavaType[] findTypeParameters(Class<?> cls, Class<?> cls2, TypeBindings typeBindings) {
        HierarchicType _findSuperTypeChain = _findSuperTypeChain(cls, cls2);
        if (_findSuperTypeChain == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Class ");
            sb.append(cls.getName());
            sb.append(" is not a subtype of ");
            sb.append(cls2.getName());
            throw new IllegalArgumentException(sb.toString());
        }
        while (_findSuperTypeChain.getSuperType() != null) {
            _findSuperTypeChain = _findSuperTypeChain.getSuperType();
            Class rawClass = _findSuperTypeChain.getRawClass();
            TypeBindings typeBindings2 = new TypeBindings(this, rawClass);
            if (_findSuperTypeChain.isGeneric()) {
                Type[] actualTypeArguments = _findSuperTypeChain.asGeneric().getActualTypeArguments();
                TypeVariable[] typeParameters = rawClass.getTypeParameters();
                int length = actualTypeArguments.length;
                for (int i = 0; i < length; i++) {
                    typeBindings2.addBinding(typeParameters[i].getName(), _constructType(actualTypeArguments[i], typeBindings));
                }
            }
            typeBindings = typeBindings2;
        }
        if (!_findSuperTypeChain.isGeneric()) {
            return null;
        }
        return typeBindings.typesAsArray();
    }

    public JavaType moreSpecificType(JavaType javaType, JavaType javaType2) {
        if (javaType == null) {
            return javaType2;
        }
        if (javaType2 == null) {
            return javaType;
        }
        Class rawClass = javaType.getRawClass();
        Class rawClass2 = javaType2.getRawClass();
        return (rawClass != rawClass2 && rawClass.isAssignableFrom(rawClass2)) ? javaType2 : javaType;
    }

    public JavaType constructType(Type type) {
        return _constructType(type, null);
    }

    public JavaType constructType(Type type, TypeBindings typeBindings) {
        return _constructType(type, typeBindings);
    }

    public JavaType constructType(TypeReference<?> typeReference) {
        return _constructType(typeReference.getType(), null);
    }

    public JavaType constructType(Type type, Class<?> cls) {
        return _constructType(type, cls == null ? null : new TypeBindings(this, cls));
    }

    public JavaType constructType(Type type, JavaType javaType) {
        return _constructType(type, javaType == null ? null : new TypeBindings(this, javaType));
    }

    /* access modifiers changed from: protected */
    public JavaType _constructType(Type type, TypeBindings typeBindings) {
        JavaType javaType;
        if (type instanceof Class) {
            javaType = _fromClass((Class) type, typeBindings);
        } else if (type instanceof ParameterizedType) {
            javaType = _fromParamType((ParameterizedType) type, typeBindings);
        } else if (type instanceof JavaType) {
            return (JavaType) type;
        } else {
            if (type instanceof GenericArrayType) {
                javaType = _fromArrayType((GenericArrayType) type, typeBindings);
            } else if (type instanceof TypeVariable) {
                javaType = _fromVariable((TypeVariable) type, typeBindings);
            } else if (type instanceof WildcardType) {
                javaType = _fromWildcard((WildcardType) type, typeBindings);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Unrecognized Type: ");
                sb.append(type == null ? "[null]" : type.toString());
                throw new IllegalArgumentException(sb.toString());
            }
        }
        if (this._modifiers != null && !javaType.isContainerType()) {
            for (TypeModifier modifyType : this._modifiers) {
                javaType = modifyType.modifyType(javaType, type, typeBindings, this);
            }
        }
        return javaType;
    }

    public ArrayType constructArrayType(Class<?> cls) {
        return ArrayType.construct(_constructType(cls, null), null, null);
    }

    public ArrayType constructArrayType(JavaType javaType) {
        return ArrayType.construct(javaType, null, null);
    }

    public CollectionType constructCollectionType(Class<? extends Collection> cls, Class<?> cls2) {
        return CollectionType.construct(cls, constructType((Type) cls2));
    }

    public CollectionType constructCollectionType(Class<? extends Collection> cls, JavaType javaType) {
        return CollectionType.construct(cls, javaType);
    }

    public CollectionLikeType constructCollectionLikeType(Class<?> cls, Class<?> cls2) {
        return CollectionLikeType.construct(cls, constructType((Type) cls2));
    }

    public CollectionLikeType constructCollectionLikeType(Class<?> cls, JavaType javaType) {
        return CollectionLikeType.construct(cls, javaType);
    }

    public MapType constructMapType(Class<? extends Map> cls, JavaType javaType, JavaType javaType2) {
        return MapType.construct(cls, javaType, javaType2);
    }

    public MapType constructMapType(Class<? extends Map> cls, Class<?> cls2, Class<?> cls3) {
        return MapType.construct(cls, constructType((Type) cls2), constructType((Type) cls3));
    }

    public MapLikeType constructMapLikeType(Class<?> cls, JavaType javaType, JavaType javaType2) {
        return MapLikeType.construct(cls, javaType, javaType2);
    }

    public MapLikeType constructMapLikeType(Class<?> cls, Class<?> cls2, Class<?> cls3) {
        return MapType.construct(cls, constructType((Type) cls2), constructType((Type) cls3));
    }

    public JavaType constructSimpleType(Class<?> cls, JavaType[] javaTypeArr) {
        TypeVariable[] typeParameters = cls.getTypeParameters();
        if (typeParameters.length != javaTypeArr.length) {
            StringBuilder sb = new StringBuilder();
            sb.append("Parameter type mismatch for ");
            sb.append(cls.getName());
            sb.append(": expected ");
            sb.append(typeParameters.length);
            sb.append(" parameters, was given ");
            sb.append(javaTypeArr.length);
            throw new IllegalArgumentException(sb.toString());
        }
        String[] strArr = new String[typeParameters.length];
        int length = typeParameters.length;
        for (int i = 0; i < length; i++) {
            strArr[i] = typeParameters[i].getName();
        }
        SimpleType simpleType = new SimpleType(cls, strArr, javaTypeArr, null, null, false);
        return simpleType;
    }

    public JavaType uncheckedSimpleType(Class<?> cls) {
        return new SimpleType(cls);
    }

    public JavaType constructParametricType(Class<?> cls, Class<?>... clsArr) {
        int length = clsArr.length;
        JavaType[] javaTypeArr = new JavaType[length];
        for (int i = 0; i < length; i++) {
            javaTypeArr[i] = _fromClass(clsArr[i], null);
        }
        return constructParametricType(cls, javaTypeArr);
    }

    public JavaType constructParametricType(Class<?> cls, JavaType... javaTypeArr) {
        if (cls.isArray()) {
            if (javaTypeArr.length == 1) {
                return constructArrayType(javaTypeArr[0]);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Need exactly 1 parameter type for arrays (");
            sb.append(cls.getName());
            sb.append(")");
            throw new IllegalArgumentException(sb.toString());
        } else if (Map.class.isAssignableFrom(cls)) {
            if (javaTypeArr.length == 2) {
                return constructMapType(cls, javaTypeArr[0], javaTypeArr[1]);
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Need exactly 2 parameter types for Map types (");
            sb2.append(cls.getName());
            sb2.append(")");
            throw new IllegalArgumentException(sb2.toString());
        } else if (!Collection.class.isAssignableFrom(cls)) {
            return constructSimpleType(cls, javaTypeArr);
        } else {
            if (javaTypeArr.length == 1) {
                return constructCollectionType(cls, javaTypeArr[0]);
            }
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Need exactly 1 parameter type for Collection types (");
            sb3.append(cls.getName());
            sb3.append(")");
            throw new IllegalArgumentException(sb3.toString());
        }
    }

    public CollectionType constructRawCollectionType(Class<? extends Collection> cls) {
        return CollectionType.construct(cls, unknownType());
    }

    public CollectionLikeType constructRawCollectionLikeType(Class<?> cls) {
        return CollectionLikeType.construct(cls, unknownType());
    }

    public MapType constructRawMapType(Class<? extends Map> cls) {
        return MapType.construct(cls, unknownType(), unknownType());
    }

    public MapLikeType constructRawMapLikeType(Class<?> cls) {
        return MapLikeType.construct(cls, unknownType(), unknownType());
    }

    /* access modifiers changed from: protected */
    public JavaType _fromClass(Class<?> cls, TypeBindings typeBindings) {
        JavaType javaType;
        JavaType javaType2;
        JavaType simpleType;
        if (cls == String.class) {
            return CORE_TYPE_STRING;
        }
        if (cls == Boolean.TYPE) {
            return CORE_TYPE_BOOL;
        }
        if (cls == Integer.TYPE) {
            return CORE_TYPE_INT;
        }
        if (cls == Long.TYPE) {
            return CORE_TYPE_LONG;
        }
        ClassKey classKey = new ClassKey(cls);
        synchronized (this._typeCache) {
            javaType = (JavaType) this._typeCache.get(classKey);
        }
        if (javaType != null) {
            return javaType;
        }
        if (cls.isArray()) {
            javaType2 = ArrayType.construct(_constructType(cls.getComponentType(), null), null, null);
        } else {
            if (cls.isEnum()) {
                simpleType = new SimpleType(cls);
            } else if (Map.class.isAssignableFrom(cls)) {
                javaType2 = _mapType(cls);
            } else if (Collection.class.isAssignableFrom(cls)) {
                javaType2 = _collectionType(cls);
            } else {
                simpleType = new SimpleType(cls);
            }
            javaType2 = simpleType;
        }
        synchronized (this._typeCache) {
            this._typeCache.put(classKey, javaType2);
        }
        return javaType2;
    }

    /* access modifiers changed from: protected */
    public JavaType _fromParameterizedClass(Class<?> cls, List<JavaType> list) {
        if (cls.isArray()) {
            return ArrayType.construct(_constructType(cls.getComponentType(), null), null, null);
        }
        if (cls.isEnum()) {
            return new SimpleType(cls);
        }
        if (Map.class.isAssignableFrom(cls)) {
            if (list.size() <= 0) {
                return _mapType(cls);
            }
            return MapType.construct(cls, (JavaType) list.get(0), list.size() >= 2 ? (JavaType) list.get(1) : _unknownType());
        } else if (Collection.class.isAssignableFrom(cls)) {
            if (list.size() >= 1) {
                return CollectionType.construct(cls, (JavaType) list.get(0));
            }
            return _collectionType(cls);
        } else if (list.size() == 0) {
            return new SimpleType(cls);
        } else {
            return constructSimpleType(cls, (JavaType[]) list.toArray(new JavaType[list.size()]));
        }
    }

    /* access modifiers changed from: protected */
    public JavaType _fromParamType(ParameterizedType parameterizedType, TypeBindings typeBindings) {
        int i;
        JavaType[] javaTypeArr;
        Class cls = (Class) parameterizedType.getRawType();
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        if (actualTypeArguments == null) {
            i = 0;
        } else {
            i = actualTypeArguments.length;
        }
        if (i == 0) {
            javaTypeArr = NO_TYPES;
        } else {
            JavaType[] javaTypeArr2 = new JavaType[i];
            for (int i2 = 0; i2 < i; i2++) {
                javaTypeArr2[i2] = _constructType(actualTypeArguments[i2], typeBindings);
            }
            javaTypeArr = javaTypeArr2;
        }
        if (Map.class.isAssignableFrom(cls)) {
            JavaType[] findTypeParameters = findTypeParameters(constructSimpleType(cls, javaTypeArr), Map.class);
            if (findTypeParameters.length == 2) {
                return MapType.construct(cls, findTypeParameters[0], findTypeParameters[1]);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Could not find 2 type parameters for Map class ");
            sb.append(cls.getName());
            sb.append(" (found ");
            sb.append(findTypeParameters.length);
            sb.append(")");
            throw new IllegalArgumentException(sb.toString());
        } else if (Collection.class.isAssignableFrom(cls)) {
            JavaType[] findTypeParameters2 = findTypeParameters(constructSimpleType(cls, javaTypeArr), Collection.class);
            if (findTypeParameters2.length == 1) {
                return CollectionType.construct(cls, findTypeParameters2[0]);
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Could not find 1 type parameter for Collection class ");
            sb2.append(cls.getName());
            sb2.append(" (found ");
            sb2.append(findTypeParameters2.length);
            sb2.append(")");
            throw new IllegalArgumentException(sb2.toString());
        } else if (i == 0) {
            return new SimpleType(cls);
        } else {
            return constructSimpleType(cls, javaTypeArr);
        }
    }

    /* access modifiers changed from: protected */
    public JavaType _fromArrayType(GenericArrayType genericArrayType, TypeBindings typeBindings) {
        return ArrayType.construct(_constructType(genericArrayType.getGenericComponentType(), typeBindings), null, null);
    }

    /* access modifiers changed from: protected */
    public JavaType _fromVariable(TypeVariable<?> typeVariable, TypeBindings typeBindings) {
        if (typeBindings == null) {
            return _unknownType();
        }
        String name = typeVariable.getName();
        JavaType findType = typeBindings.findType(name);
        if (findType != null) {
            return findType;
        }
        Type[] bounds = typeVariable.getBounds();
        typeBindings._addPlaceholder(name);
        return _constructType(bounds[0], typeBindings);
    }

    /* access modifiers changed from: protected */
    public JavaType _fromWildcard(WildcardType wildcardType, TypeBindings typeBindings) {
        return _constructType(wildcardType.getUpperBounds()[0], typeBindings);
    }

    private JavaType _mapType(Class<?> cls) {
        JavaType[] findTypeParameters = findTypeParameters(cls, Map.class);
        if (findTypeParameters == null) {
            return MapType.construct(cls, _unknownType(), _unknownType());
        }
        if (findTypeParameters.length == 2) {
            return MapType.construct(cls, findTypeParameters[0], findTypeParameters[1]);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Strange Map type ");
        sb.append(cls.getName());
        sb.append(": can not determine type parameters");
        throw new IllegalArgumentException(sb.toString());
    }

    private JavaType _collectionType(Class<?> cls) {
        JavaType[] findTypeParameters = findTypeParameters(cls, Collection.class);
        if (findTypeParameters == null) {
            return CollectionType.construct(cls, _unknownType());
        }
        if (findTypeParameters.length == 1) {
            return CollectionType.construct(cls, findTypeParameters[0]);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Strange Collection type ");
        sb.append(cls.getName());
        sb.append(": can not determine type parameters");
        throw new IllegalArgumentException(sb.toString());
    }

    /* access modifiers changed from: protected */
    public JavaType _resolveVariableViaSubTypes(HierarchicType hierarchicType, String str, TypeBindings typeBindings) {
        if (hierarchicType != null && hierarchicType.isGeneric()) {
            TypeVariable[] typeParameters = hierarchicType.getRawClass().getTypeParameters();
            int length = typeParameters.length;
            for (int i = 0; i < length; i++) {
                if (str.equals(typeParameters[i].getName())) {
                    Type type = hierarchicType.asGeneric().getActualTypeArguments()[i];
                    if (type instanceof TypeVariable) {
                        return _resolveVariableViaSubTypes(hierarchicType.getSubType(), ((TypeVariable) type).getName(), typeBindings);
                    }
                    return _constructType(type, typeBindings);
                }
            }
        }
        return _unknownType();
    }

    /* access modifiers changed from: protected */
    public JavaType _unknownType() {
        return new SimpleType(Object.class);
    }

    /* access modifiers changed from: protected */
    public HierarchicType _findSuperTypeChain(Class<?> cls, Class<?> cls2) {
        if (cls2.isInterface()) {
            return _findSuperInterfaceChain(cls, cls2);
        }
        return _findSuperClassChain(cls, cls2);
    }

    /* access modifiers changed from: protected */
    public HierarchicType _findSuperClassChain(Type type, Class<?> cls) {
        HierarchicType hierarchicType = new HierarchicType(type);
        Class<?> rawClass = hierarchicType.getRawClass();
        if (rawClass == cls) {
            return hierarchicType;
        }
        Type genericSuperclass = rawClass.getGenericSuperclass();
        if (genericSuperclass != null) {
            HierarchicType _findSuperClassChain = _findSuperClassChain(genericSuperclass, cls);
            if (_findSuperClassChain != null) {
                _findSuperClassChain.setSubType(hierarchicType);
                hierarchicType.setSuperType(_findSuperClassChain);
                return hierarchicType;
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public HierarchicType _findSuperInterfaceChain(Type type, Class<?> cls) {
        HierarchicType hierarchicType = new HierarchicType(type);
        Class<ArrayList> rawClass = hierarchicType.getRawClass();
        if (rawClass == cls) {
            return new HierarchicType(type);
        }
        if (rawClass == HashMap.class && cls == Map.class) {
            return _hashMapSuperInterfaceChain(hierarchicType);
        }
        if (rawClass == ArrayList.class && cls == List.class) {
            return _arrayListSuperInterfaceChain(hierarchicType);
        }
        return _doFindSuperInterfaceChain(hierarchicType, cls);
    }

    /* access modifiers changed from: protected */
    public HierarchicType _doFindSuperInterfaceChain(HierarchicType hierarchicType, Class<?> cls) {
        Class rawClass = hierarchicType.getRawClass();
        Type[] genericInterfaces = rawClass.getGenericInterfaces();
        if (genericInterfaces != null) {
            for (Type _findSuperInterfaceChain : genericInterfaces) {
                HierarchicType _findSuperInterfaceChain2 = _findSuperInterfaceChain(_findSuperInterfaceChain, cls);
                if (_findSuperInterfaceChain2 != null) {
                    _findSuperInterfaceChain2.setSubType(hierarchicType);
                    hierarchicType.setSuperType(_findSuperInterfaceChain2);
                    return hierarchicType;
                }
            }
        }
        Type genericSuperclass = rawClass.getGenericSuperclass();
        if (genericSuperclass != null) {
            HierarchicType _findSuperInterfaceChain3 = _findSuperInterfaceChain(genericSuperclass, cls);
            if (_findSuperInterfaceChain3 != null) {
                _findSuperInterfaceChain3.setSubType(hierarchicType);
                hierarchicType.setSuperType(_findSuperInterfaceChain3);
                return hierarchicType;
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public synchronized HierarchicType _hashMapSuperInterfaceChain(HierarchicType hierarchicType) {
        if (this._cachedHashMapType == null) {
            HierarchicType deepCloneWithoutSubtype = hierarchicType.deepCloneWithoutSubtype();
            _doFindSuperInterfaceChain(deepCloneWithoutSubtype, Map.class);
            this._cachedHashMapType = deepCloneWithoutSubtype.getSuperType();
        }
        HierarchicType deepCloneWithoutSubtype2 = this._cachedHashMapType.deepCloneWithoutSubtype();
        hierarchicType.setSuperType(deepCloneWithoutSubtype2);
        deepCloneWithoutSubtype2.setSubType(hierarchicType);
        return hierarchicType;
    }

    /* access modifiers changed from: protected */
    public synchronized HierarchicType _arrayListSuperInterfaceChain(HierarchicType hierarchicType) {
        if (this._cachedArrayListType == null) {
            HierarchicType deepCloneWithoutSubtype = hierarchicType.deepCloneWithoutSubtype();
            _doFindSuperInterfaceChain(deepCloneWithoutSubtype, List.class);
            this._cachedArrayListType = deepCloneWithoutSubtype.getSuperType();
        }
        HierarchicType deepCloneWithoutSubtype2 = this._cachedArrayListType.deepCloneWithoutSubtype();
        hierarchicType.setSuperType(deepCloneWithoutSubtype2);
        deepCloneWithoutSubtype2.setSubType(hierarchicType);
        return hierarchicType;
    }
}