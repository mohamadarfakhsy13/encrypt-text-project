#include <jni.h>
#include <string>
#include <vector>
#include <android/log.h>

#define LOG_TAG "NativeLib"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

/**
 * Checks for JNI exceptions and clears them if present.
 * Returns true if an exception was detected.
 */
bool checkException(JNIEnv* env) {
    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
        return true;
    }
    return false;
}

/**
 * Helper to convert a jstring to a true UTF-8 std::string.
 * Uses String.getBytes("UTF-8") to avoid "Modified UTF-8" issues.
 */
std::string jstringToStdString(JNIEnv* env, jstring jstr) {
    if (jstr == nullptr) return "";

    jclass stringClass = env->FindClass("java/lang/String");
    jmethodID getBytesMethod = env->GetMethodID(stringClass, "getBytes", "(Ljava/lang/String;)[B");
    jstring charsetName = env->NewStringUTF("UTF-8");

    jbyteArray bytes = (jbyteArray)env->CallObjectMethod(jstr, getBytesMethod, charsetName);

    jsize length = env->GetArrayLength(bytes);
    jbyte* bytePtr = env->GetByteArrayElements(bytes, nullptr);

    std::string result((char*)bytePtr, length);

    env->ReleaseByteArrayElements(bytes, bytePtr, JNI_ABORT);
    env->DeleteLocalRef(charsetName);
    env->DeleteLocalRef(stringClass);
    env->DeleteLocalRef(bytes);

    return result;
}

/**
 * Downloads a URL by calling a Kotlin method.
 */
std::string downloadUrlViaJNI(JNIEnv* env, jobject thiz, const char* urlStr) {
    jclass clazz = env->GetObjectClass(thiz);
    jmethodID downloadMethod = env->GetMethodID(clazz, "downloadUrlKotlin", "(Ljava/lang/String;)Ljava/lang/String;");
    if (checkException(env) || downloadMethod == nullptr) {
        env->DeleteLocalRef(clazz);
        return "Error: JNI Callback Setup Failed";
    }

    jstring jUrlStr = env->NewStringUTF(urlStr);
    jstring jResult = (jstring)env->CallObjectMethod(thiz, downloadMethod, jUrlStr);
    if (checkException(env)) {
        env->DeleteLocalRef(clazz);
        env->DeleteLocalRef(jUrlStr);
        return "Error: JNI Execution Failed";
    }

    std::string result = jstringToStdString(env, jResult);

    env->DeleteLocalRef(clazz);
    env->DeleteLocalRef(jUrlStr);
    env->DeleteLocalRef(jResult);

    return result;
}

std::string extractJsonValue(const std::string& json, const std::string& key) {
    std::string searchKey = "\"" + key + "\":\"";
    size_t startPos = json.find(searchKey);
    if (startPos == std::string::npos) {
        searchKey = "\"" + key + "\": \"";
        startPos = json.find(searchKey);
    }
    if (startPos == std::string::npos) return "";

    startPos += searchKey.length();
    size_t endPos = json.find("\"", startPos);
    if (endPos == std::string::npos) return "";

    return json.substr(startPos, endPos - startPos);
}

/**
 * Encrypts a string using RSA-OAEP-256 by orchestrating system crypto providers via JNI.
 */
std::string encryptWithRsaNativeOrchestrated(JNIEnv* env, const std::string& nBase64, const std::string& eBase64, const std::string& input) {
    jclass base64Class = env->FindClass("android/util/Base64");
    jmethodID decodeMethod = env->GetStaticMethodID(base64Class, "decode", "(Ljava/lang/String;I)[B");
    jint flags = 10; // URL_SAFE | NO_PADDING

    jstring jN = env->NewStringUTF(nBase64.c_str());
    jstring jE = env->NewStringUTF(eBase64.c_str());

    jbyteArray nBytes = (jbyteArray)env->CallStaticObjectMethod(base64Class, decodeMethod, jN, flags);
    jbyteArray eBytes = (jbyteArray)env->CallStaticObjectMethod(base64Class, decodeMethod, jE, flags);

    jclass bigIntClass = env->FindClass("java/math/BigInteger");
    jmethodID bigIntConstructor = env->GetMethodID(bigIntClass, "<init>", "(I[B)V");
    jobject nBigInt = env->NewObject(bigIntClass, bigIntConstructor, 1, nBytes);
    jobject eBigInt = env->NewObject(bigIntClass, bigIntConstructor, 1, eBytes);

    jclass rsaSpecClass = env->FindClass("java/security/spec/RSAPublicKeySpec");
    jmethodID rsaSpecConstructor = env->GetMethodID(rsaSpecClass, "<init>", "(Ljava/math/BigInteger;Ljava/math/BigInteger;)V");
    jobject rsaSpec = env->NewObject(rsaSpecClass, rsaSpecConstructor, nBigInt, eBigInt);

    jclass keyFactoryClass = env->FindClass("java/security/KeyFactory");
    jmethodID getInstanceMethod = env->GetStaticMethodID(keyFactoryClass, "getInstance", "(Ljava/lang/String;)Ljava/security/KeyFactory;");
    jobject keyFactory = env->CallStaticObjectMethod(keyFactoryClass, getInstanceMethod, env->NewStringUTF("RSA"));
    jmethodID generatePublicMethod = env->GetMethodID(keyFactoryClass, "generatePublic", "(Ljava/security/spec/KeySpec;)Ljava/security/PublicKey;");
    jobject publicKey = env->CallObjectMethod(keyFactory, generatePublicMethod, rsaSpec);

    jclass cipherClass = env->FindClass("javax/crypto/Cipher");
    jmethodID cipherGetInstance = env->GetStaticMethodID(cipherClass, "getInstance", "(Ljava/lang/String;)Ljavax/crypto/Cipher;");
    jobject cipher = env->CallStaticObjectMethod(cipherClass, cipherGetInstance, env->NewStringUTF("RSA/ECB/OAEPWithSHA-256AndMGF1Padding"));

    jmethodID cipherInit = env->GetMethodID(cipherClass, "init", "(ILjava/security/Key;)V");
    env->CallVoidMethod(cipher, cipherInit, 1, publicKey); // 1 = ENCRYPT_MODE

    jbyteArray inputBytes = env->NewByteArray(input.length());
    env->SetByteArrayRegion(inputBytes, 0, input.length(), (const jbyte*)input.c_str());

    jmethodID doFinalMethod = env->GetMethodID(cipherClass, "doFinal", "([B)[B");
    jbyteArray encryptedBytes = (jbyteArray)env->CallObjectMethod(cipher, doFinalMethod, inputBytes);

    jmethodID encodeMethod = env->GetStaticMethodID(base64Class, "encodeToString", "([BI)Ljava/lang/String;");
    jstring resultJStr = (jstring)env->CallStaticObjectMethod(base64Class, encodeMethod, encryptedBytes, (jint)2); // NO_WRAP

    std::string result = jstringToStdString(env, resultJStr);

    // Clean up all local refs
    env->DeleteLocalRef(base64Class);
    env->DeleteLocalRef(jN);
    env->DeleteLocalRef(jE);
    env->DeleteLocalRef(nBytes);
    env->DeleteLocalRef(eBytes);
    env->DeleteLocalRef(bigIntClass);
    env->DeleteLocalRef(nBigInt);
    env->DeleteLocalRef(eBigInt);
    env->DeleteLocalRef(rsaSpecClass);
    env->DeleteLocalRef(rsaSpec);
    env->DeleteLocalRef(keyFactoryClass);
    env->DeleteLocalRef(keyFactory);
    env->DeleteLocalRef(publicKey);
    env->DeleteLocalRef(cipherClass);
    env->DeleteLocalRef(cipher);
    env->DeleteLocalRef(inputBytes);
    env->DeleteLocalRef(encryptedBytes);
    env->DeleteLocalRef(resultJStr);

    return result;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_arfakhsy_pixcels_1assessment_data_datasource_NativeEncryptionDataSourceImpl_encryptAndSend(
        JNIEnv* env,
        jobject thiz,
        jstring input) {

    std::string inputStr = jstringToStdString(env, input);

    LOGI("NativeCore: Initiating public key download...");
    std::string jsonResponse = downloadUrlViaJNI(env, thiz, "https://sandbox.api.piperks.com/.well-known/pi-xcels.json");

    if (jsonResponse.empty() || jsonResponse.find("Error") == 0) {
        LOGE("NativeCore: Failed to download public key.");
        return env->NewStringUTF("Error: Could not retrieve public key.");
    }

    std::string n = extractJsonValue(jsonResponse, "n");
    std::string e = extractJsonValue(jsonResponse, "e");

    if (n.empty() || e.empty()) {
        return env->NewStringUTF("Error: Invalid key format.");
    }

    LOGI("NativeCore: Encrypting...");
    std::string ciphertext;
    try {
        ciphertext = encryptWithRsaNativeOrchestrated(env, n, e, inputStr);
    } catch (...) {
        return env->NewStringUTF("Error: Encryption failed.");
    }

    return env->NewStringUTF(ciphertext.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_arfakhsy_pixcels_1assessment_data_datasource_NativeEncryptionDataSourceImpl_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    return env->NewStringUTF("Hello from Pi-Xcels Native Core");
}
