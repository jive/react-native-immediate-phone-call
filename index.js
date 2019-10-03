import {NativeModules} from 'react-native';

var RNImmediatePhoneCall = {
  immediatePhoneCall: async function(number) {
    return NativeModules.RNImmediatePhoneCall.immediatePhoneCall(number);
  }
};

export default RNImmediatePhoneCall;
