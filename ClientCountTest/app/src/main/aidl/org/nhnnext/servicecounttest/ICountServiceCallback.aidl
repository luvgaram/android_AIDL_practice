// ICountServiceCallback.aidl
package org.nhnnext.servicecounttest;

interface ICountServiceCallback {
    oneway void onCountChanged (int changedCount);
}
