package jp.co.willwave.aca.constants;

public enum AddEditModeEnum {
    ADD(0), EDIT(1), CLONE(2);

    private Integer mode;

    AddEditModeEnum(Integer mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }


    public static boolean isAddMode(Integer mode) {
        return (mode == null || AddEditModeEnum.ADD.mode.equals(mode));
    }

    public static boolean isEditMode(Integer mode) {
        return AddEditModeEnum.EDIT.mode.equals(mode);
    }

    public static boolean isCloneMode(Integer mode) {
        return AddEditModeEnum.CLONE.mode.equals(mode);
    }
}
