LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libvirtualmouse
LOCAL_SRC_FILES := virtualmouse.cpp
#LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog
LOCAL_SHARED_LIBRARIES := libcutils libutils
LOCAL_CERTIFICATE := platform
include $(BUILD_SHARED_LIBRARY)
