#include <jni.h>
#include <stdio.h>
#include <android/log.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>
#include <android/log.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <sys/stat.h>
#include <linux/input.h>
#include <cutils/log.h>
#include <uinput.h>
extern "C" {
JNIEXPORT jstring JNICALL Java_com_casky_sLD8_1smart_1rc_1server_Virtual_1Mouse_1Server_stringFromJNI
  (JNIEnv * env, jobject);
JNIEXPORT jstring JNICALL Java_com_casky_sLD8_1smart_1rc_1server_Virtual_1Mouse_1Server_closeDevice
  (JNIEnv * env, jobject);
JNIEXPORT void JNICALL Java_com_casky_sLD8_1smart_1rc_1server_Virtual_1Mouse_1Server_sendMsgToJni
  (JNIEnv *, jobject, jint, jint, jint);
}
struct uinput_user_dev uidev;
struct input_event event;
int uinput_fd = -1;
int  setup_uinput_device(char *dev);
void kbdms_open_dev();
void kbdms_send_mouse_move_events(unsigned short is_rel, short pos_x, short pos_y);
void kbdms_send_button_event(unsigned short key_code, int is_pressed_or_release);
void kbdms_send_wheel_events(short wheel_value);
void kbdms_close_dev(void);
int reportkey(int fd, uint16_t type, uint16_t keycode, int32_t value);

JNIEXPORT jstring JNICALL Java_com_casky_sLD8_1smart_1rc_1server_Virtual_1Mouse_1Server_stringFromJNI
  (JNIEnv * env, jobject)
{
	kbdms_open_dev();
	return env->NewStringUTF("Hello World");
}
JNIEXPORT jstring JNICALL Java_com_casky_sLD8_1smart_1rc_1server_Virtual_1Mouse_1Server_closeDevice
  (JNIEnv * env, jobject)
{
	return env->NewStringUTF("Good bye");
}
JNIEXPORT void JNICALL Java_com_casky_sLD8_1smart_1rc_1server_Virtual_1Mouse_1Server_sendMsgToJni
  (JNIEnv *, jobject, jint act, jint x, jint y)
{
	switch(act)
	{
		case 0:
			kbdms_send_mouse_move_events(1,x,y);
			break;
		case 1:
			kbdms_send_button_event(BTN_LEFT,1);
			kbdms_send_button_event(BTN_LEFT,0);
			break;
		case 2:
			kbdms_send_button_event(KEY_RIGHT,1);
			kbdms_send_button_event(KEY_RIGHT,0);
			break;
		case 3:
			kbdms_send_wheel_events(y);
			break;
		default:
			break;
	}
}
void kbdms_open_dev(void){
  int ret = -1;
  ret = setup_uinput_device("/dev/uinput");
  if(ret < 0){
	  __android_log_print(ANDROID_LOG_INFO, "syo", "call setup_uinput_device failure");
      return;
  }
}
int setup_uinput_device(char *dev)
{
	int  ret;
	int i = 0;
	__android_log_print(ANDROID_LOG_INFO, "syo", "setup_uinput_device start");
	if(dev == NULL)
	{
		__android_log_print(ANDROID_LOG_INFO, "syo", "The device is null");
	    return -1;
	}
	if (uinput_fd > 0){
		return uinput_fd;
	}
	uinput_fd = open(dev, O_WRONLY | O_NONBLOCK);
	if (uinput_fd < 0) {
		return uinput_fd;
	}

	memset(&uidev, 0, sizeof(struct uinput_user_dev));

	snprintf(uidev.name, UINPUT_MAX_NAME_SIZE, "virtual mouse");
	uidev.id.bustype = BUS_USB;
	uidev.id.vendor = 0x1234;
	uidev.id.product = 0xfedc;
	uidev.id.version = 1;

	ioctl(uinput_fd, UI_SET_EVBIT, EV_KEY);
	ioctl(uinput_fd, UI_SET_EVBIT, EV_REL);
	ioctl(uinput_fd, UI_SET_RELBIT, REL_X);
	ioctl(uinput_fd, UI_SET_RELBIT, REL_Y);
	ioctl(uinput_fd, UI_SET_RELBIT, REL_WHEEL);
	/*
	for(i = 0; i < 256; i++){
		ioctl(uinput_fd, UI_SET_KEYBIT, i);
	  }*/
	ioctl(uinput_fd, UI_SET_KEYBIT, BTN_MOUSE);
	ioctl(uinput_fd, UI_SET_KEYBIT, BTN_TOUCH);
	ioctl(uinput_fd, UI_SET_KEYBIT, BTN_LEFT);
	ioctl(uinput_fd, UI_SET_KEYBIT, BTN_MIDDLE);
	ioctl(uinput_fd, UI_SET_KEYBIT, BTN_RIGHT);
	ioctl(uinput_fd, UI_SET_KEYBIT, BTN_FORWARD);
	ioctl(uinput_fd, UI_SET_KEYBIT, BTN_BACK);

	ret = write(uinput_fd, &uidev, sizeof(struct uinput_user_dev));

	ret = ioctl(uinput_fd, UI_DEV_CREATE);

	if (ret < 0) {
		__android_log_print(ANDROID_LOG_INFO, "syo", "close uinput_fd");
		close(uinput_fd);
		return ret;
	}
	__android_log_print(ANDROID_LOG_INFO, "syo", "setup_uinput_device end");
	return 0;
}

void kbdms_send_mouse_move_events(unsigned short is_rel, short pos_x, short pos_y){
  if(uinput_fd <= 0){
	__android_log_print(ANDROID_LOG_INFO, "syo", "please call kbdms_open_dev first!");
    kbdms_open_dev();
    if(uinput_fd <= 0)
      return;
  }
  /* Move pointer to (0,0) location */
  memset(&event, 0, sizeof(event));
  gettimeofday(&event.time, NULL);
  event.type = (is_rel == 1)?EV_REL:EV_ABS;
  event.code = (is_rel == 1)?REL_X:ABS_X;
  event.value = pos_x;
  write(uinput_fd, &event, sizeof(event));

  event.type = EV_SYN;
  event.code = SYN_REPORT;
  event.value = 0;
  write(uinput_fd, &event, sizeof(event));

  memset(&event, 0, sizeof(event));
  gettimeofday(&event.time, NULL);
  event.type = (is_rel == 1)?EV_REL:EV_ABS;
  event.code = (is_rel == 1)?REL_Y:ABS_Y;
  event.value = pos_y;
  write(uinput_fd, &event, sizeof(event));
}

void kbdms_send_button_event(unsigned short key_code, int is_pressed_or_release){

  if(uinput_fd <= 0){
	__android_log_print(ANDROID_LOG_INFO, "syo", "please call kbdms_open_dev first!!");
    kbdms_open_dev();
    if(uinput_fd <= 0)
      return;
  }
  /* report button click --press event */
  memset(&event, 0, sizeof(event));
  gettimeofday(&event.time, NULL);
  event.type = EV_KEY;
  event.code = key_code;
  event.value = is_pressed_or_release;
  write(uinput_fd, &event, sizeof(event));

  event.type = EV_SYN;
  event.code = SYN_REPORT;
  event.value = 0;
  write(uinput_fd, &event, sizeof(event));

}

void kbdms_send_wheel_events(short wheel_value){
  if(uinput_fd <= 0){
	__android_log_print(ANDROID_LOG_INFO, "syo", "please call kbdms_open_dev first!!!");
    kbdms_open_dev();
    if(uinput_fd <= 0)
      return;

  }
  /* Move pointer to (0,0) location */
  memset(&event, 0, sizeof(event));
  gettimeofday(&event.time, NULL);
  event.type = EV_REL;
  event.code = REL_WHEEL;
  event.value = wheel_value;
  write(uinput_fd, &event, sizeof(event));

  event.type = EV_SYN;
  event.code = SYN_REPORT;
  event.value = 0;
  write(uinput_fd, &event, sizeof(event));


}

int reportkey(int fd, uint16_t type, uint16_t keycode, int32_t value)
{
	struct input_event event;

	event.type = type;
	event.code = keycode;
	event.value = value;

	gettimeofday(&event.time, 0);

	if (write(fd, &event, sizeof(struct input_event)) < 0) {
		__android_log_print(ANDROID_LOG_INFO, "syo", "report key error!");
		return -1;
	}

	return 0;
}

void kbdms_close_dev(void){
  if(uinput_fd > 0){
    ioctl(uinput_fd, UI_DEV_DESTROY);
    close(uinput_fd);
    uinput_fd = -1;
  }

}
