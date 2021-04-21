package com.lockminds.tayari.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tables")
data class Table(
        @PrimaryKey(autoGenerate = false) var id: Long,
        var name: String? = "",
        var team_id: String? = "",
        var restaurant_name: String? = "",
        var restaurant_banner: String? = "",
        var restaurant_logo: String? = "",

 ): Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),) {
    }

    constructor(): this(
        1,
        "",
            "",
            "",
            "",
            ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(team_id)
        parcel.writeString(restaurant_name)
        parcel.writeString(restaurant_banner)
        parcel.writeString(restaurant_logo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Table> {
        override fun createFromParcel(parcel: Parcel): Table {
            return Table(parcel)
        }

        override fun newArray(size: Int): Array<Table?> {
            return arrayOfNulls(size)
        }
    }
}
