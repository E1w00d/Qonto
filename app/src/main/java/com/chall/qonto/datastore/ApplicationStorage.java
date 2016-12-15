package com.chall.qonto.datastore;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.StaleDataException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chall.qonto.data.ApplicationDataModule;
import com.chall.qonto.data.DatabaseHelper;
import com.chall.qonto.model.Address;
import com.chall.qonto.model.Album;
import com.chall.qonto.model.Company;
import com.chall.qonto.model.Geo;
import com.chall.qonto.model.Photo;
import com.chall.qonto.model.User;

import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public final class ApplicationStorage extends Storage {
	public ApplicationStorage(final Context context) {
		this(new ApplicationDataModule().getDatabaseHelper(context));
	}

	private ApplicationStorage(final DatabaseHelper dbHelper) {
		super(dbHelper);
	}

	public final void updateUserInTransaction(final User user) {
		final SQLiteDatabase db = this.dbHelper.getWritableDatabase();

		db.beginTransaction();

		try {
			ApplicationStorage.updateUserInTransaction(db, new ContentValues(), new String[1], user);
		}
		finally {
			db.setTransactionSuccessful();
			db.endTransaction();
		}
	}

	public final void updateUsersInTransaction(final List<User> users) {
		final SQLiteDatabase db = this.dbHelper.getWritableDatabase();

		db.beginTransaction();

		try {
			db.delete("users", null, null);

			if (null != users && 0 < users.size()) {
				final String[] whereArgs = {null,};
				final ContentValues values = new ContentValues();

				for (final User user : users) {
					ApplicationStorage.updateUserInTransaction(db, values, whereArgs, user);
				}
			}
		}
		finally {
			db.setTransactionSuccessful();
			db.endTransaction();
		}
	}

	public final void updateAlbumTransaction(@NonNull final Album album) {
		final SQLiteDatabase db = this.dbHelper.getWritableDatabase();

		db.beginTransaction();

		try {
			ApplicationStorage.updateAlbumInTransaction(db, new ContentValues(4), new String[2], album);
		}
		finally {
			db.setTransactionSuccessful();
			db.endTransaction();
		}
	}

	public final void updateAlbumsTransaction(@Nullable final List<Album> albums) {
		final SQLiteDatabase db = this.dbHelper.getWritableDatabase();

		db.beginTransaction();

		try {
			if (null != albums && 0 < albums.size()) {
				db.delete("albums", "user_id=?",
						  new String[] {String.valueOf(albums.get(0).getUserId()),});

				final String[] whereArgs = {null, null,};
				final ContentValues values = new ContentValues(4);

				for (final Album album : albums) {
					ApplicationStorage.updateAlbumInTransaction(db, values, whereArgs, album);
				}
			}
		}
		finally {
			db.setTransactionSuccessful();
			db.endTransaction();
		}
	}

	public final void updatePhotoTransaction(@NonNull final Photo photo) {
		final SQLiteDatabase db = this.dbHelper.getWritableDatabase();

		db.beginTransaction();

		try {
			ApplicationStorage.updatePhotoInTransaction(db, new ContentValues(4), new String[2], photo);
		}
		finally {
			db.setTransactionSuccessful();
			db.endTransaction();
		}
	}

	public final void updatePhotosTransaction(@Nullable final List<Photo> photos) {
		final SQLiteDatabase db = this.dbHelper.getWritableDatabase();

		db.beginTransaction();

		try {
			if (null != photos && 0 < photos.size()) {
				db.delete("photos", "album_id=?",
						  new String[] {String.valueOf(photos.get(0).getAlbumId()),});

				final String[] whereArgs = {null, null};
				final ContentValues values = new ContentValues(4);

				for (final Photo photo : photos) {
					ApplicationStorage.updatePhotoInTransaction(db, values, whereArgs, photo);
				}
			}
		}
		finally {
			db.setTransactionSuccessful();
			db.endTransaction();
		}
	}

	private static void updateUserInTransaction(final SQLiteDatabase db, final ContentValues values,
												final String[] whereArgs, final User user) {
		try {
			long addressId = 0L;
			long companyId = 0L;

			if (user.hasCompany()) {
				companyId = ApplicationStorage.updateCompanyInTransaction(db, values, whereArgs, user.getCompany());
			}

			if (user.hasAddress()) {
				addressId = ApplicationStorage.updateAddressInTransaction(db, values, new String[2], user.getAddress());
			}

			whereArgs[0] = String.valueOf(user.getUserId());

			values.put("address", addressId);
			values.put("company", companyId);

			ApplicationStorage.createUserValues(values, user);

			if (0 == db.update("users", values, "user_id=?", whereArgs)) {
				db.insert("users", null, values);
			}
		}
		catch (final SQLException | StaleDataException | IllegalStateException ex) {
			Timber.tag("Storage").wtf(ex, "Failed to update user: %s", user);
		}
		finally {
			values.clear();

			whereArgs[0] = null;
		}
	}

	private static void updateAlbumInTransaction(final SQLiteDatabase db, final ContentValues values,
												 final String[] whereArgs, final Album album) {
		try {
			whereArgs[0] = String.valueOf(album.getUserId());
			whereArgs[1] = String.valueOf(album.getAlbumId());

			ApplicationStorage.createAlbumValues(values, album);

			if (0 == db.update("albums", values, "user_id=? AND album_id=?", whereArgs)) {
				db.insert("albums", null, values);
			}
		}
		catch (final SQLException | StaleDataException | IllegalStateException ex) {
			Timber.tag("Storage").wtf(ex, "Failed to update album: %s", album);
		}
		finally {
			values.clear();

			Arrays.fill(whereArgs, null);
		}
	}

	private static void updatePhotoInTransaction(final SQLiteDatabase db, final ContentValues values,
												 final String[] whereArgs, final Photo photo) {
		try {
			whereArgs[0] = String.valueOf(photo.getAlbumId());
			whereArgs[1] = String.valueOf(photo.getPhotoId());

			ApplicationStorage.createPhotoValues(values, photo);

			if (0 == db.update("photos", values, "album_id=? AND photo_id=?", whereArgs)) {
				db.insert("photos", null, values);
			}
		}
		catch (final SQLException | StaleDataException | IllegalStateException ex) {
			Timber.tag("Storage").wtf(ex, "Failed to update photo: %s", photo);
		}
		finally {
			values.clear();

			Arrays.fill(whereArgs, null);
		}
	}

	private static long updateCompanyInTransaction(final SQLiteDatabase db, final ContentValues values,
												   final String[] whereArgs, final Company company) {
		long rowId = 0L;

		try {
			whereArgs[0] = company.getName();

			ApplicationStorage.createCompanyValues(values, company);

			if (0 == (rowId = db.update("company", values, "name=?", whereArgs))) {
				rowId = db.insert("company", null, values);
			}
		}
		catch (final SQLException | StaleDataException | IllegalStateException ex) {
			Timber.tag("Storage").wtf(ex, "Failed to update company: %s", company);
		}
		finally {
			values.clear();

			whereArgs[0] = null;
		}

		return rowId;
	}

	private static long updateAddressInTransaction(final SQLiteDatabase db, final ContentValues values,
												   final String[] whereArgs, final Address address) {
		long rowId = 0L;

		try {
			if (address.hasGeo()) {
				final Geo geo = address.getGeo();

				whereArgs[0] = String.valueOf(geo.getLatitude());
				whereArgs[1] = String.valueOf(geo.getLongitude());
			}

			ApplicationStorage.createAddressValues(values, address);

			if (0 == (rowId = db.update("address", values, "lat=? AND lng=?", whereArgs))) {
				rowId = db.insert("address", null, values);
			}
		}
		catch (final SQLException | StaleDataException | IllegalStateException ex) {
			Timber.tag("Storage").wtf(ex, "Failed to update address: %s", address);
		}
		finally {
			values.clear();

			Arrays.fill(whereArgs, null);
		}

		return rowId;
	}

	private static void createAlbumValues(final ContentValues values, final Album album) {
		values.put("title", album.getTitle());

		values.put("user_id", album.getUserId());
		values.put("album_id", album.getAlbumId());
	}

	private static void createPhotoValues(final ContentValues values, final Photo photo) {
		values.put("album_id", photo.getAlbumId());
		values.put("photo_id", photo.getPhotoId());

		values.put("title", photo.getTitle());

		values.put("url", photo.getPhotoUrl());
		values.put("thumbnail", photo.getThumbnailUrl());
	}

	private static void createUserValues(final ContentValues values, final User user) {
		values.put("user_id", user.getUserId());

		values.put("full_name", user.getFullName());
		values.put("last_name", user.getLastName());
		values.put("first_name", user.getFirstName());

		values.put("email", user.getEmail());
		values.put("phone", user.getPhone());
		values.put("website", user.getWebsite());
	}

	private static void createCompanyValues(final ContentValues values, final Company company) {
		values.put("bs", company.getBs());
		values.put("name", company.getName());
		values.put("catch_phrase", company.getCatchPhrase());
	}

	private static void createAddressValues(final ContentValues values, final Address address) {
		values.put("city", address.getCity());
		values.put("suite", address.getSuite());
		values.put("street", address.getStreet());
		values.put("zip_code", address.getZipCode());

		if (address.hasGeo()) {
			final Geo geo = address.getGeo();

			values.put("lat", geo.getLatitude());
			values.put("lng", geo.getLongitude());
		}
		else {
			values.put("lat", .0D);
			values.put("lng", .0D);
		}
	}
}
