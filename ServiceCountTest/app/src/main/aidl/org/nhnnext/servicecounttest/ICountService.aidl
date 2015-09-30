// ICountService.aidl
package org.nhnnext.servicecounttest;
import org.nhnnext.servicecounttest.ICountServiceCallback;

interface ICountService {
    int getCurNumber();
    int sum(int a, int b);

    boolean registerCountCallback(ICountServiceCallback callback);
    boolean unregisterCountCallback(ICountServiceCallback callback);
}
