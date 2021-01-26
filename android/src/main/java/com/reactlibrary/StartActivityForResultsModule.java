package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.Callback;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import android.util.SparseArray;
import com.facebook.react.ReactActivity;

//implements ActivityEventListener 
public class StartActivityForResultsModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    public static final String TAG = "StartActivityForResults";
    final SparseArray<Promise> mPromises;
    Activity activity;

    public StartActivityForResultsModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.reactContext.addActivityEventListener(mActivityEventListener);
        this.reactContext.addLifecycleEventListener(mResume);
        mPromises = new SparseArray<>();
    }

    LifecycleEventListener mResume  = new LifecycleEventListener(){

        @Override
        public void onHostResume() {
            // Activity `onResume`
            //Log.d(TAG, "App has resumed");
         }

         @Override
        public void onHostPause() {
            //Log.d(TAG, "App has paused");
        }

        @Override
        public void onHostDestroy() {
            //Log.d(TAG, "App has destroyed");
        }
    };

    //ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
    ActivityEventListener mActivityEventListener = new ActivityEventListener(){

        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            //super.onActivityResult(requestCode, resCode, data);
    
            Log.d(TAG,"On activity result");
            Promise promise = mPromises.get(requestCode);
            if(promise != null){
                WritableMap result = new WritableNativeMap();
                if (resultCode == activity.RESULT_OK) {
                    Log.d(TAG,"Promise is not null and result code is OK");
                    
                    result.putInt("resultCode", resultCode);
                    result.putMap("data", Arguments.makeNativeMap(data.getExtras()));
                    
                }
                else{
                    Log.d(TAG,"Promise and intent data are empty");
                    result.putString("Results", "Promise and intent data are empty");
                }
    
                if(resultCode == activity.RESULT_CANCELED ){
                    Log.d(TAG,"Result cancelled or no result or crashed with code");
                    result.putString("Results", "result cancelled or no result or crashed with code");
                }
    
                promise.resolve(result);
            }
            
        }

        @Override
        public void onNewIntent(Intent intent){
            Log.d(TAG,"New Intent");
        }

    };

    @Override
    public String getName() {
        return "StartActivityForResults";
    }

    @ReactMethod
    public void launchApp(String stringArgument, ReadableMap args, Promise promise) throws JSONException{

        
        try {
            final JSONObject options = convertMapToJson(args);
            Bundle extras = new Bundle();;
            int requestCode = 0;

            if (options.has("extras")) {
                extras = createExtras(options.getJSONArray("extras"));
                Log.d(TAG,"Extras found");
                Log.d(TAG, options.getString("extras"));
            } else {
                extras = new Bundle();
                Log.d(TAG,"No extras");
            }

            if (options.has("launchRequestCode")) {
                requestCode = options.getInt("launchRequestCode");
            }
            else{
                promise.resolve(null);
                return;
            }

            Intent packageIntent = new Intent(stringArgument);
            packageIntent.putExtras(extras);
            Activity activity = getReactApplicationContext().getCurrentActivity();
            activity.startActivityForResult(packageIntent, requestCode);
            mPromises.put(requestCode, promise);
            
        } catch (JSONException e) {
            //TODO: handle exception
            Log.d(TAG, e.toString());
        }
        
    }

    private static JSONObject convertMapToJson(ReadableMap readableMap) throws JSONException {
        JSONObject object = new JSONObject();
        
        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            switch (readableMap.getType(key).toString()) {
                case "Null":
                    object.put(key, JSONObject.NULL);
                    break;
                case "Boolean":
                    object.put(key, readableMap.getBoolean(key));
                    break;
                case "Number":
                    object.put(key, readableMap.getDouble(key));
                    break;
                case "String":
                    object.put(key, readableMap.getString(key));
                    break;
                case "Map":
                    object.put(key, convertMapToJson(readableMap.getMap(key)));
                    break;
                case "Array":
                    object.put(key, convertArrayToJson(readableMap.getArray(key)));
                    break;
            }
        }
        return object;
    }

    private static JSONArray convertArrayToJson(ReadableArray readableArray) throws JSONException {
        JSONArray array = new JSONArray();
        for (int i = 0; i < readableArray.size(); i++) {
            switch (readableArray.getType(i).toString()) {
                case "Null":
                    break;
                case "Boolean":
                    array.put(readableArray.getBoolean(i));
                    break;
                case "Number":
                    array.put(readableArray.getDouble(i));
                    break;
                case "String":
                    array.put(readableArray.getString(i));
                    break;
                case "Map":
                    array.put(convertMapToJson(readableArray.getMap(i)));
                    break;
                case "Array":
                    array.put(convertArrayToJson(readableArray.getArray(i)));
                    break;
            }
        }
        return array;
    }

    private Bundle createExtras(JSONArray extrasObj) throws JSONException {
		Bundle extras = new Bundle();
		try {
            for (int i = 0, size = extrasObj.length(); i < size; i++) {
                JSONObject extra = extrasObj.getJSONObject(i);
                if (extra.has("name") && extra.has("value") && extra.has("dataType")) {
                    String extraName = extra.getString("name");
                    String dataType = extra.getString("dataType");
                    try {
                        if (dataType.equalsIgnoreCase("Byte")) {
                            try {
                                extras.putByte(extraName, ((byte) extra.getInt("value")));
                            } catch (Exception e) {
                                Log.e(TAG, "Error converting to byte for extra: " + extraName);
                                e.printStackTrace();
                                throw e;
                            }
                        } else if (dataType.equalsIgnoreCase("ByteArray")) {
                            try {
                                extras.putByteArray(extraName, ParseTypes.toByteArray(extra.getJSONArray("value")));
                            } catch (Exception e) {
                                Log.e(TAG, "Error converting to byte for extra: " + extraName);
                                e.printStackTrace();
                                throw e;
                            }
                        } else if (dataType.equalsIgnoreCase("Short")) {
                            try {
                                extras.putShort(extraName, ((short) extra.getInt("value")));
                            } catch (Exception e) {
                                Log.e(TAG, "Error converting to short for extra: " + extraName);
                                e.printStackTrace();
                                throw e;
                            }
                        } else if (dataType.equalsIgnoreCase("ShortArray")) {
                            extras.putShortArray(extraName, ParseTypes.toShortArray(extra.getJSONArray("value")));
                        } else if (dataType.equalsIgnoreCase("Int")) {
                            extras.putInt(extraName, extra.getInt("value"));
                        } else if (dataType.equalsIgnoreCase("IntArray")) {
                            extras.putIntArray(extraName, ParseTypes.toIntArray(extra.getJSONArray("value")));
                        } else if (dataType.equalsIgnoreCase("IntArrayList")) {
                            extras.putIntegerArrayList(extraName, ParseTypes.toIntegerArrayList(extra.getJSONArray("value")));
                        } else if (dataType.equalsIgnoreCase("Long")) {
                            extras.putLong(extraName, extra.getLong("value"));
                        } else if (dataType.equalsIgnoreCase("LongArray")) {
                            extras.putLongArray(extraName, ParseTypes.toLongArray(extra.getJSONArray("value")));
                        } else if (dataType.equalsIgnoreCase("Float")) {
                            try {
                                extras.putFloat(extraName, Float.parseFloat(extra.getString("value")));
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing float for extra: " + extraName);
                                e.printStackTrace();
                                throw e;
                            }
                        } else if (dataType.equalsIgnoreCase("FloatArray")) {
                            try {
                                extras.putFloatArray(extraName, ParseTypes.toFloatArray(extra.getJSONArray("value")));
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing float for extra: " + extraName);
                                e.printStackTrace();
                                throw e;
                            }
                        } else if (dataType.equalsIgnoreCase("Double")) {
                            extras.putDouble(extraName, extra.getDouble("value"));
                        } else if (dataType.equalsIgnoreCase("DoubleArray")) {
                            extras.putDoubleArray(extraName, ParseTypes.toDoubleArray(extra.getJSONArray("value")));
                        } else if (dataType.equalsIgnoreCase("Boolean")) {
                            extras.putBoolean(extraName, extra.getBoolean("value"));
                        } else if (dataType.equalsIgnoreCase("BooleanArray")) {
                            extras.putBooleanArray(extraName, ParseTypes.toBooleanArray(extra.getJSONArray("value")));
                        } else if (dataType.equalsIgnoreCase("String")) {
                            extras.putString(extraName, extra.getString("value"));
                        } else if (dataType.equalsIgnoreCase("StringArray")) {
                            extras.putStringArray(extraName, ParseTypes.toStringArray(extra.getJSONArray("value")));
                        } else if (dataType.equalsIgnoreCase("StringArrayList")) {
                            extras.putStringArrayList(extraName, ParseTypes.toStringArrayList(extra.getJSONArray("value")));
                        } else if (dataType.equalsIgnoreCase("Char")) {
                            extras.putChar(extraName, ParseTypes.toChar(extra.getString("value")));
                        } else if (dataType.equalsIgnoreCase("CharArray")) {
                            extras.putCharArray(extraName, ParseTypes.toCharArray(extra.getString("value")));
                        } else if (dataType.equalsIgnoreCase("CharSequence")) {
                            extras.putCharSequence(extraName, extra.getString("value"));
                        } else if (dataType.equalsIgnoreCase("CharSequenceArray")) {
                            extras.putCharSequenceArray(extraName, ParseTypes.toCharSequenceArray(extra.getJSONArray("value")));
                        } else if (dataType.equalsIgnoreCase("CharSequenceArrayList")) {
                            extras.putCharSequenceArrayList(extraName, ParseTypes.toCharSequenceArrayList(extra.getJSONArray("value")));
                        /*
                        } else if (dataType.equalsIgnoreCase("Size") && Build.VERSION.SDK_INT >= 21) {
                            extras.putSize(extraName, extra.getJSONObject("value"));
                        } else if (dataType.equalsIgnoreCase("SizeF") && Build.VERSION.SDK_INT >= 21) {
                            extras.putSizeF(extraName, extra.getJSONObject("value"));
                        */
                        } else if (dataType.toLowerCase().contains("parcelable")) {
                            if (!extra.has("paType")) {
                                Log.e(TAG, "Property 'paType' must be provided if dataType is " + dataType + ".");
                                throw new Exception("Missing property paType.");
                            } else {
                                String paType = extra.getString("paType").toUpperCase();
                                if (ParseTypes.SUPPORTED_PA_TYPES.contains(paType)) {
                                    if (dataType.equalsIgnoreCase("Parcelable")) {
                                        extras.putParcelable(extraName, ParseTypes.toParcelable(extra.getString("value"), paType));
                                    } else if (dataType.equalsIgnoreCase("ParcelableArray")) {
                                        extras.putParcelableArray(extraName, ParseTypes.toParcelableArray(extra.getJSONArray("value"), paType));
                                    } else if (dataType.equalsIgnoreCase("ParcelableArrayList")) {
                                        extras.putParcelableArrayList(extraName, ParseTypes.toParcelableArrayList(extra.getJSONArray("value"), paType));
                                    } else if (dataType.equalsIgnoreCase("SparseParcelableArray")) {
                                        extras.putSparseParcelableArray(extraName, ParseTypes.toSparseParcelableArray(extra.getJSONObject("value"), paType));
                                    }
                                } else {
                                    Log.e(TAG, "ParcelableArray type '" + paType + "' is not currently supported.");
                                    throw new Exception("Provided parcelable array type not supported.");
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing extra. Skipping: " + extraName);
                    }
                } else {
                    Log.e(TAG, "Extras must have a name, value, and datatype.");
                }
            }
        } catch (JSONException e) {
            //TODO: handle exception
            Log.e(TAG, e.toString());
        }

		Log.d(TAG, "EXTRAS");
		Log.d(TAG, "" + extras);

        return extras;
    }
}
