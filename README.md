# react-native-start-activity-for-results

## Getting started

`$ npm install react-native-start-activity-for-results --save`

### Mostly automatic installation

`$ react-native link react-native-start-activity-for-results`

### Fetching from Git
`$git+https://github.com/Tebogo-Legodi/StartActivityForResults.git`

## Usage
```javascript
import StartActivityForResults from 'react-native-start-activity-for-results';

// TODO: What to do with the module?
async launchApp(){
    let JsonBody = {
        extras:[
            {
                name: "DeviceID",
                value: "Tebogo",
                dataType: "String"
            },
            {
                name: "Role",
                value: 10,
                dataType: "Int"
            }
        ],
        launchRequestCode: 2
    };
    const activityName = 'com.bld.pushnotification.Main';
    const response = await StartActivityForResults.launchApp(activityName,JsonBody);
    console.log(response);  

    //Compare the resultCode against Native Android result code
    if (response.resultCode !== -1) {
        throw new Error('Invalid result from activity.');
    } else {
        console.log('Got the following response: ');
        console.log(JSON.stringfy(response.data));
    }
    
}
```

# Extras Data Types

Most datatypes that can be put into an Android Bundle are able to be passed in. You must provide the datatype to convert to.
Only Uri Parcelables are supported currently.
```javascript
	extras: [
		{"name":"myByte", "value":1, "dataType":"Byte"},
		{"name":"myByteArray", "value":[1,0,2,3], "dataType":"ByteArray"},
		{"name":"myShort", "value":5, "dataType":"Short"},
		{"name":"myShortArray", "value":[1,2,3,4], "dataType":"ShortArray"},
		{"name":"myInt", "value":2000, "dataType":"Int"},
		{"name":"myIntArray", "value":[12,34,56], "dataType":"IntArray"},
		{"name":"myIntArrayList", "value":[123,456,789], "dataType":"IntArrayList"},
		{"name":"myLong", "value":123456789101112, "dataType":"Long"},
		{"name":"myLongArray", "value":[123456789101112,121110987654321], "dataType":"LongArray"},
		{"name":"myFloat", "value":12.34, "dataType":"Float"},
		{"name":"myFloatArray", "value":[12.34,56.78], "dataType":"FloatArray"},
		{"name":"myDouble", "value":12.3456789, "dataType":"Double"},
		{"name":"myDoubleArray", "value":[12.3456789, 98.7654321], "dataType":"DoubleArray"},
		{"name":"myBoolean", "value":false, "dataType":"Boolean"},
		{"name":"myBooleanArray", "value":[true,false,true], "dataType":"BooleanArray"},
		{"name":"myString", "value":"this is a test", "dataType":"String"},
		{"name":"myStringArray", "value":["this","is", "a", "test"], "dataType":"StringArray"},
		{"name":"myStringArrayList", "value":["this","is","a","test"], "dataType":"StringArrayList"},
		{"name":"myChar", "value":"T", "dataType":"Char"},
		{"name":"myCharArray", "value":"this is a test", "dataType":"CharArray"},
		{"name":"myCharSequence", "value":"this is a test", "dataType":"CharSequence"},
		{"name":"myCharSequenceArray", "value":["this","is a", "test"], "dataType":"CharSequenceArray"},
		{"name":"myCharSequenceArrayList", "value":["this","is a", "test"], "dataType":"CharSequenceArrayList"},
		{"name":"myParcelable", "value":"http://foo", "dataType":"Parcelable", "paType":"Uri"},
		{"name":"myParcelableArray", "value":["http://foo","http://bar"], "dataType":"ParcelableArray", "paType":"Uri"},
		{"name":"myParcelableArrayList", "value":["http://foo","http://bar"], "dataType":"ParcelableArrayList", "paType":"Uri"},
		{"name":"mySparseParcelableArray", "value":{"10":"http://foo", "-25":"http://bar"}, "dataType":"SparseParcelableArray", "paType":"Uri"},
	]
```
