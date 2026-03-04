package fr.ufrst.m1info.comp5.type_checker;

import fr.ufrst.m1info.comp5.memory.Type;

public enum MjType {
    RIEN("void", Void.class, Type.VOID),
    ENTIER("int", Integer.class,Type.ENTIER),
    BOOL("boolean", Boolean.class,Type.BOOL);

    private final String label;
    private final Class<?> expectedClass;
    private final Type correspondingType;

    MjType(String label, Class<?> expectedClass, Type correspondingType) {
        this.label = label;
        this.expectedClass = expectedClass;
        this.correspondingType = correspondingType;
    }

    public static MjType fromLabel(String label) {
        for (MjType t : values()) {
            if (t.label.equals(label)) {
                return t;
            }
        }
        return null;
    }

    public static String getLabelFromType(Type type) {
        for (MjType mjType : values()) {
            if (mjType.correspondingType.equals(type)) {
                return mjType.label;
            }
        }
        return null;
    }

    public Class<?> getExpectedClass() {
        return expectedClass;
    }

    public String getLabel() {
        return label;
    }

    public Type toType() {
        return this.correspondingType;
    }
}