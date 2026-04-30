#include <jni.h>
#include <windows.h>
#include <mmsystem.h>
#include <dwmapi.h>
#include <map>
#include <mutex>

#pragma comment(lib, "winmm.lib")
#pragma comment(lib, "dwmapi.lib")

// Global map to store Java callbacks for timers
struct TimerContext {
    JavaVM* jvm;
    jobject callbackRef;
};

std::map<UINT, TimerContext*> g_timerContexts;
std::mutex g_timerMutex;

void CALLBACK TimerProc(UINT uID, UINT uMsg, DWORD_PTR dwUser, DWORD_PTR dw1, DWORD_PTR dw2) {
    TimerContext* ctx = (TimerContext*)dwUser;
    JNIEnv* env;
    
    // Attach current thread to JVM if necessary
    jint res = ctx->jvm->AttachCurrentThreadAsDaemon((void**)&env, NULL);
    if (res == JNI_OK) {
        jclass runnableClass = env->GetObjectClass(ctx->callbackRef);
        jmethodID runMethod = env->GetMethodID(runnableClass, "run", "()V");
        env->CallVoidMethod(ctx->callbackRef, runMethod);
        // env->DetachCurrentThread(); // Keep attached as daemon for performance
    }
}

extern "C" {

JNIEXPORT jboolean JNICALL Java_fastdwm_FastDWM_beginTimerPeriod(JNIEnv* env, jclass clazz, jint res) {
    return timeBeginPeriod(res) == TIMERR_NOERROR;
}

JNIEXPORT jboolean JNICALL Java_fastdwm_FastDWM_endTimerPeriod(JNIEnv* env, jclass clazz, jint res) {
    return timeEndPeriod(res) == TIMERR_NOERROR;
}

JNIEXPORT jint JNICALL Java_fastdwm_FastDWM_createPeriodicTimer(JNIEnv* env, jclass clazz, jint delay, jobject callback) {
    JavaVM* jvm;
    env->GetJavaVM(&jvm);

    TimerContext* ctx = new TimerContext();
    ctx->jvm = jvm;
    ctx->callbackRef = env->NewGlobalRef(callback);

    UINT timerId = timeSetEvent(delay, 1, TimerProc, (DWORD_PTR)ctx, TIME_PERIODIC);
    
    if (timerId != 0) {
        std::lock_guard<std::mutex> lock(g_timerMutex);
        g_timerContexts[timerId] = ctx;
    } else {
        env->DeleteGlobalRef(ctx->callbackRef);
        delete ctx;
    }

    return (jint)timerId;
}

JNIEXPORT jboolean JNICALL Java_fastdwm_FastDWM_killTimer(JNIEnv* env, jclass clazz, jint timerId) {
    MMRESULT res = timeKillEvent((UINT)timerId);
    
    std::lock_guard<std::mutex> lock(g_timerMutex);
    auto it = g_timerContexts.find((UINT)timerId);
    if (it != g_timerContexts.end()) {
        env->DeleteGlobalRef(it->second->callbackRef);
        delete it->second;
        g_timerContexts.erase(it);
    }

    return res == TIMERR_NOERROR;
}

JNIEXPORT void JNICALL Java_fastdwm_FastDWM_waitForVSync(JNIEnv* env, jclass clazz) {
    DwmFlush();
}

}
