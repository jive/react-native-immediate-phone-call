import {NativeModules, Platform, PermissionsAndroid} from 'react-native';

const isAndroid = Platform.OS === 'android';

const defaultTitle = "Request Camera Permission";
const defaultMessage = "Grant access to enable calling your phone";

var RNImmediatePhoneCall = {
  immediatePhoneCall: async function(number, title = defaultTitle, message = defaultMessage, buttonNegative = 'Cancel', buttonPositive = 'OK') {
    if (isAndroid) {
      try {
        let permission = await PermissionsAndroid.check(PermissionsAndroid.PERMISSIONS.CALL_PHONE);
        if(!permission) {
          let granted = await requestCallPermission(title, message, buttonNegative, buttonPositive);
          if (granted) {
            return NativeModules.RNImmediatePhoneCall.immediatePhoneCall(number);
          }
          return Promise.reject("Permission not granted");
        }
      } catch (e) {
        return Promise.reject(e);
      }
    }
    return NativeModules.RNImmediatePhoneCall.immediatePhoneCall(number);
  }
};

async function requestCallPermission(title, message, buttonNegative, buttonPositive) {
  try {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.CALL_PHONE,
      {
        title,
        message,
        buttonNegative,
        buttonPositive
      },
    );
    if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      return true;
    } else {
      return false;
    }
  } catch (err) {
    return false;
  }
}

export default RNImmediatePhoneCall;