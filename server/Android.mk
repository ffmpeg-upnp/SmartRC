LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := $(call all-subdir-java-files)
LOCAL_PACKAGE_NAME := sLD8_Smart_RC_Server
LOCAL_JNI_SHARED_LIBRARIES := libvirtualmouse
LOCAL_REQUIRED_MODULES := libvirtualmouse
LOCAL_SDK_VERSION := current
LOCAL_CERTIFICATE := platform
include $(BUILD_PACKAGE)
MY_PATH := $(LOCAL_PATH)
include $(MY_PATH)/jni/Android.mk
