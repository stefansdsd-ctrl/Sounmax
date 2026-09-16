package com.example.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.media.CallOneTap
import com.example.media.FlightOneTap
import com.example.media.FocusOneTap
import com.example.media.GameOneTap
import com.example.media.GymOneTap
import com.example.media.KitchenOneTap
import com.example.media.MovieOneTap
import com.example.media.PodcastOneTap
import com.example.media.RainOneTap
import com.example.media.SleepOneTap
import com.example.media.WalkOneTap
import com.example.media.CommuteOneTap
import com.example.media.OfficeOneTap
import com.example.media.LibraryOneTap
import com.example.media.CafeOneTap
import com.example.media.MeetingOneTap
import com.example.media.BikeOneTap
import com.example.media.ShopOneTap
import com.example.media.KidsOneTap
import com.example.media.TrainOneTap
import com.example.media.CarOneTap
import com.example.media.MetroOneTap
import com.example.media.ConcertOneTap
import com.example.media.RestaurantOneTap
import com.example.media.CollegeOneTap
import com.example.media.MuseumOneTap
import com.example.media.BuildOneTap
import com.example.media.BeachOneTap
import com.example.media.ZoomOneTap
import com.example.media.YogaOneTap
import com.example.media.OffOneTap
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun ContextChipsBar() {
    val context = LocalContext.current
    var callOn by remember { mutableStateOf(CallOneTap.isOn(context)) }
    var podOn by remember { mutableStateOf(PodcastOneTap.isOn(context)) }
    var flightOn by remember { mutableStateOf(FlightOneTap.isOn(context)) }
    var flightLeft by remember { mutableStateOf(FlightOneTap.remainingMin(context)) }
    var gameOn by remember { mutableStateOf(GameOneTap.isOn(context)) }
    var movieOn by remember { mutableStateOf(MovieOneTap.isOn(context)) }
    var rainOn by remember { mutableStateOf(RainOneTap.isOn(context)) }
    var kitchenOn by remember { mutableStateOf(KitchenOneTap.isOn(context)) }
    var walkOn by remember { mutableStateOf(WalkOneTap.isOn(context)) }
    var focusOn by remember { mutableStateOf(FocusOneTap.isOn(context)) }
    var gymOn by remember { mutableStateOf(GymOneTap.isOn(context)) }
    var sleepOn by remember { mutableStateOf(SleepOneTap.isOn(context)) }
    var commuteOn by remember { mutableStateOf(CommuteOneTap.isOn(context)) }
    var officeOn by remember { mutableStateOf(OfficeOneTap.isOn(context)) }
    var libraryOn by remember { mutableStateOf(LibraryOneTap.isOn(context)) }
    var cafeOn by remember { mutableStateOf(CafeOneTap.isOn(context)) }
    var meetingOn by remember { mutableStateOf(MeetingOneTap.isOn(context)) }
    var bikeOn by remember { mutableStateOf(BikeOneTap.isOn(context)) }
    var shopOn by remember { mutableStateOf(ShopOneTap.isOn(context)) }
    var kidsOn by remember { mutableStateOf(KidsOneTap.isOn(context)) }
    var trainOn by remember { mutableStateOf(TrainOneTap.isOn(context)) }
    var carOn by remember { mutableStateOf(CarOneTap.isOn(context)) }
    var metroOn by remember { mutableStateOf(MetroOneTap.isOn(context)) }
    var concertOn by remember { mutableStateOf(ConcertOneTap.isOn(context)) }
    var restaurantOn by remember { mutableStateOf(RestaurantOneTap.isOn(context)) }
    var collegeOn by remember { mutableStateOf(CollegeOneTap.isOn(context)) }
    var museumOn by remember { mutableStateOf(MuseumOneTap.isOn(context)) }
    var buildOn by remember { mutableStateOf(BuildOneTap.isOn(context)) }
    var beachOn by remember { mutableStateOf(BeachOneTap.isOn(context)) }
    var zoomOn by remember { mutableStateOf(ZoomOneTap.isOn(context)) }
    var yogaOn by remember { mutableStateOf(YogaOneTap.isOn(context)) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag("context_chips_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Chip(label = if (callOn) "Bel aan" else "Bel", selected = callOn, tag = "call_chip") {
            CallOneTap.toggle(context); callOn = CallOneTap.isOn(context)
        }
        Chip(label = if (podOn) "Podcast aan" else "Podcast", selected = podOn, tag = "podcast_chip") {
            PodcastOneTap.toggle(context); podOn = PodcastOneTap.isOn(context)
        }
        Chip(label = if (flightOn) "Vlucht ${flightLeft}m" else "Vliegtuig", selected = flightOn, tag = "flight_chip") {
            FlightOneTap.toggle(context); flightOn = FlightOneTap.isOn(context); flightLeft = FlightOneTap.remainingMin(context)
        }
        Chip(label = if (gameOn) "Game aan" else "Game", selected = gameOn, tag = "game_chip") {
            GameOneTap.toggle(context); gameOn = GameOneTap.isOn(context)
        }
        Chip(label = if (movieOn) "Film aan" else "Film", selected = movieOn, tag = "movie_chip") {
            MovieOneTap.toggle(context); movieOn = MovieOneTap.isOn(context)
        }
        Chip(label = if (rainOn) "Regen aan" else "Regen", selected = rainOn, tag = "rain_chip") {
            RainOneTap.toggle(context); rainOn = RainOneTap.isOn(context)
        }
        Chip(label = if (kitchenOn) "Koken aan" else "Koken", selected = kitchenOn, tag = "kitchen_chip") {
            KitchenOneTap.toggle(context); kitchenOn = KitchenOneTap.isOn(context)
        }
        Chip(label = if (walkOn) "Wandelen aan" else "Wandelen", selected = walkOn, tag = "walk_chip") {
            WalkOneTap.toggle(context); walkOn = WalkOneTap.isOn(context)
        }
        Chip(label = if (focusOn) "Focus aan" else "Focus", selected = focusOn, tag = "focus_chip") {
            FocusOneTap.toggle(context); focusOn = FocusOneTap.isOn(context)
        }
        Chip(label = if (gymOn) "Sport aan" else "Sport", selected = gymOn, tag = "gym_chip") {
            GymOneTap.toggle(context); gymOn = GymOneTap.isOn(context)
        }
        Chip(label = if (sleepOn) "Slaap aan" else "Slaap", selected = sleepOn, tag = "sleep_chip") {
            SleepOneTap.toggle(context); sleepOn = SleepOneTap.isOn(context)
        }
        Chip(label = if (commuteOn) "Pendelen aan" else "Pendelen", selected = commuteOn, tag = "commute_chip") {
            CommuteOneTap.toggle(context); commuteOn = CommuteOneTap.isOn(context)
        }
        Chip(label = if (officeOn) "Kantoor aan" else "Kantoor", selected = officeOn, tag = "office_chip") {
            OfficeOneTap.toggle(context); officeOn = OfficeOneTap.isOn(context)
        }
        Chip(label = if (libraryOn) "Bieb aan" else "Bieb", selected = libraryOn, tag = "library_chip") {
            LibraryOneTap.toggle(context); libraryOn = LibraryOneTap.isOn(context)
        }
        Chip(label = if (cafeOn) "Café aan" else "Café", selected = cafeOn, tag = "cafe_chip") {
            CafeOneTap.toggle(context); cafeOn = CafeOneTap.isOn(context)
        }
        Chip(label = if (meetingOn) "Vergadering aan" else "Vergadering", selected = meetingOn, tag = "meeting_chip") {
            MeetingOneTap.toggle(context); meetingOn = MeetingOneTap.isOn(context)
        }
        Chip(label = if (bikeOn) "Fiets aan" else "Fiets", selected = bikeOn, tag = "bike_chip") {
            BikeOneTap.toggle(context); bikeOn = BikeOneTap.isOn(context)
        }
        Chip(label = if (shopOn) "Winkelen aan" else "Winkelen", selected = shopOn, tag = "shop_chip") {
            ShopOneTap.toggle(context); shopOn = ShopOneTap.isOn(context)
        }
        Chip(label = if (kidsOn) "Kids aan" else "Kids", selected = kidsOn, tag = "kids_chip") {
            KidsOneTap.toggle(context); kidsOn = KidsOneTap.isOn(context)
        }
        Chip(label = if (trainOn) "Trein aan" else "Trein", selected = trainOn, tag = "train_chip") {
            TrainOneTap.toggle(context); trainOn = TrainOneTap.isOn(context)
        }
        Chip(label = if (carOn) "Auto aan" else "Auto", selected = carOn, tag = "car_chip") {
            CarOneTap.toggle(context); carOn = CarOneTap.isOn(context)
        }
        Chip(label = if (metroOn) "Metro aan" else "Metro", selected = metroOn, tag = "metro_chip") {
            MetroOneTap.toggle(context); metroOn = MetroOneTap.isOn(context)
        }
        Chip(label = if (concertOn) "Concert aan" else "Concert", selected = concertOn, tag = "concert_chip") {
            ConcertOneTap.toggle(context); concertOn = ConcertOneTap.isOn(context)
        }
        Chip(label = if (restaurantOn) "Eten aan" else "Eten", selected = restaurantOn, tag = "restaurant_chip") {
            RestaurantOneTap.toggle(context); restaurantOn = RestaurantOneTap.isOn(context)
        }
        Chip(label = if (collegeOn) "College aan" else "College", selected = collegeOn, tag = "college_chip") {
            CollegeOneTap.toggle(context); collegeOn = CollegeOneTap.isOn(context)
        }
        Chip(label = if (museumOn) "Museum aan" else "Museum", selected = museumOn, tag = "museum_chip") {
            MuseumOneTap.toggle(context); museumOn = MuseumOneTap.isOn(context)
        }
        Chip(label = if (buildOn) "Bouw aan" else "Bouw", selected = buildOn, tag = "build_chip") {
            BuildOneTap.toggle(context); buildOn = BuildOneTap.isOn(context)
        }
        Chip(label = if (beachOn) "Strand aan" else "Strand", selected = beachOn, tag = "beach_chip") {
            BeachOneTap.toggle(context); beachOn = BeachOneTap.isOn(context)
        }
        Chip(label = if (zoomOn) "Call aan" else "Videocall", selected = zoomOn, tag = "zoom_chip") {
            ZoomOneTap.toggle(context); zoomOn = ZoomOneTap.isOn(context)
        }
        Chip(label = if (yogaOn) "Yoga aan" else "Yoga", selected = yogaOn, tag = "yoga_chip") {
            YogaOneTap.toggle(context); yogaOn = YogaOneTap.isOn(context)
        }
        Chip(label = "Uit", selected = false, tag = "off_chip") {
            OffOneTap.toggle(context)
            collegeOn = CollegeOneTap.isOn(context)
            museumOn = MuseumOneTap.isOn(context)
            buildOn = BuildOneTap.isOn(context)
            beachOn = BeachOneTap.isOn(context)
            zoomOn = ZoomOneTap.isOn(context)
            yogaOn = YogaOneTap.isOn(context)
        }
    }
}

@Composable
private fun Chip(label: String, selected: Boolean, tag: String, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 11.sp, maxLines = 1) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
            containerColor = ImmersiveSurfaceActive,
            labelColor = ImmersiveTextSecondary,
            selectedLabelColor = ImmersiveLavenderAccent
        ),
        modifier = Modifier.testTag(tag)
    )
}
