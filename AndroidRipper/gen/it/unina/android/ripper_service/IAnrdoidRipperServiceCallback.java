/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/nicola/dev/ar-github/AndroidRipper/src/it/unina/android/ripper_service/IAnrdoidRipperServiceCallback.aidl
 */
package it.unina.android.ripper_service;
public interface IAnrdoidRipperServiceCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements it.unina.android.ripper_service.IAnrdoidRipperServiceCallback
{
private static final java.lang.String DESCRIPTOR = "it.unina.android.ripper_service.IAnrdoidRipperServiceCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an it.unina.android.ripper_service.IAnrdoidRipperServiceCallback interface,
 * generating a proxy if needed.
 */
public static it.unina.android.ripper_service.IAnrdoidRipperServiceCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof it.unina.android.ripper_service.IAnrdoidRipperServiceCallback))) {
return ((it.unina.android.ripper_service.IAnrdoidRipperServiceCallback)iin);
}
return new it.unina.android.ripper_service.IAnrdoidRipperServiceCallback.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_receive:
{
data.enforceInterface(DESCRIPTOR);
java.util.Map _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
this.receive(_arg0);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements it.unina.android.ripper_service.IAnrdoidRipperServiceCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void receive(java.util.Map message) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(message);
mRemote.transact(Stub.TRANSACTION_receive, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_receive = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void receive(java.util.Map message) throws android.os.RemoteException;
}
