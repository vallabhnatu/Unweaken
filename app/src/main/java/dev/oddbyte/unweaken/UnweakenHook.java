package dev.oddbyte.unweaken;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import org.json.JSONObject;

public class UnweakenHook implements IXposedHookLoadPackage {

    private static final ThreadLocal<Boolean> isHooked = ThreadLocal.withInitial(() -> Boolean.FALSE);

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("Unweaken Hook Initialized for: " + lpparam.packageName);

        // Hook String.contains(CharSequence)
        XposedHelpers.findAndHookMethod(
                "java.lang.String",
                lpparam.classLoader,
                "contains",
                CharSequence.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (isHooked.get()) {
                            return;  // Skip the hook if it's already been processed
                        }
                        try {
                            if (param.args[0] instanceof CharSequence) {
                                CharSequence argument = (CharSequence) param.args[0];
                                isHooked.set(Boolean.TRUE);
                                if (argument != null && argument.toString().matches(".*MEETS_.*") && argument.toString().matches(".*_INTEGRITY.*")) {
                                    XposedBridge.log("Hooked String.contains for argument: " + argument);
                                    param.setResult(true); // Always return true
                                }
                            }
                            isHooked.set(Boolean.FALSE);
                        }
                        catch (Throwable e) {
                            isHooked.set(Boolean.FALSE);
                            XposedBridge.log(e);
                        }
                    }
                }
        );

        // Hook String.matches(String)
        XposedHelpers.findAndHookMethod(
                "java.lang.String",
                lpparam.classLoader,
                "matches",
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (isHooked.get()) {
                            return;  // Skip the hook if it's already been processed
                        }
                        try {
                            String regex = (String) param.args[0];
                            isHooked.set(Boolean.TRUE);
                            if (regex != null && !regex.matches("\\.\\*MEETS\\.\\*") && regex.matches(".*MEETS_.*") && regex.matches(".*_INTEGRITY.*")) {
                                XposedBridge.log("Hooked String.matches for regex: " + regex);
                                param.setResult(true); // Always return true
                            }
                            isHooked.set(Boolean.FALSE);
                        }
                        catch (Throwable e) {
                            isHooked.set(Boolean.FALSE);
                            XposedBridge.log(e);
                        }
                    }
                }
        );

        // Hook String.equals(Object)
        XposedHelpers.findAndHookMethod(
                "java.lang.String",
                lpparam.classLoader,
                "equals",
                Object.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            if (isHooked.get()) {
                                return;  // Skip the hook if it's already been processed
                            }
                            Object comparison = param.args[0];
                            if (comparison instanceof String) {
                                String value = (String) comparison;
                                isHooked.set(Boolean.TRUE);
                                if (value.matches(".*MEETS_.*") && value.matches(".*_INTEGRITY.*")) {
                                    XposedBridge.log("Hooked String.equals for value: " + value);
                                    param.setResult(true); // Always return true
                                }
                                isHooked.set(Boolean.FALSE);
                            }
                        }
                        catch (Throwable e) {
                            isHooked.set(Boolean.FALSE);
                            XposedBridge.log(e);
                        }
                    }
                }
        );

        // Hook JSONObject.has(String)
        try {
            // Check for the JSONObject class
            Class<?> jsonObjectClass = XposedHelpers.findClass("org.json.JSONObject", lpparam.classLoader);
            if (jsonObjectClass != null) {
                XposedHelpers.findAndHookMethod(
                        jsonObjectClass,
                        "has",
                        String.class,
                        new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                if (isHooked.get()) {
                                    return;  // Skip the hook if it's already been processed
                                }
                                try {
                                    String key = (String) param.args[0];
                                    isHooked.set(Boolean.TRUE);
                                    if (key != null && key.matches(".*MEETS_.*") && key.matches(".*_INTEGRITY.*")) {
                                        XposedBridge.log("Hooked JSONObject.has for key: " + key);
                                        param.setResult(true); // Always return true
                                    }
                                    isHooked.set(Boolean.FALSE);
                                }
                                catch (Throwable e) {
                                    isHooked.set(Boolean.FALSE);
                                    XposedBridge.log(e);
                                }
                            }
                        }
                );
            }
        } catch (Throwable e) {
            isHooked.set(Boolean.FALSE);
            XposedBridge.log("Failed to hook org.json.JSONObject: " + e.getMessage());
        }
    }
}
