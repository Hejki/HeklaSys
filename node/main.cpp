#include <Arduino.h>
#include "HeklaSys.h"

int main(void) {
	init();

	HeklaSys sys;

	sys.setup();
	for (;;)
		sys.loop();

	return 0;
}
