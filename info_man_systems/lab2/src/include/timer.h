#ifndef __INCLUDE_TIMER_H
#define __INCLUDE_TIMER_H

void InitTimer();                        //Инициализация таймера

unsigned long GetMsCounter();            //Получение текущей метки времени в милисекундах

unsigned long DTimeMs(unsigned long t0); //Измерение количества милисекунд прошедших 
                                         //с временной метки t0 и до текущего времени
										 
void DelayMs(unsigned long t);           //Задержка на t милисекунд

#endif