import numpy as np
import matplotlib.mlab as mlab
import matplotlib.pyplot as plt
from scipy.ndimage.filters import maximum_filter
from scipy.ndimage.morphology import (generate_binary_structure,
									  iterate_structure, binary_erosion)
import hashlib
import os
import decoder


IDX_FREQ_I = 0
IDX_TIME_J = 1

DEFAULT_FS = 44100
DEFAULT_WINDOW_SIZE = 4096
DEFAULT_OVERLAP_RATIO = 0.5
DEFAULT_FAN_VALUE = 15

DEFAULT_AMP_MIN = 10
PEAK_NEIGHBORHOOD_SIZE = 20
MIN_HASH_TIME_DELTA = 0
MAX_HASH_TIME_DELTA = 200

def fingerprint(channel_samples, Fs=DEFAULT_FS,
				wsize=DEFAULT_WINDOW_SIZE,
				wratio=DEFAULT_OVERLAP_RATIO,
				fan_value=DEFAULT_FAN_VALUE,
				amp_min=DEFAULT_AMP_MIN):
	"""
	FFT the channel, log transform output, find local maxima, then return
	locally sensitive hashes.
	"""
	# FFT the signal and extract frequency components
	arr2D = mlab.specgram(
		channel_samples,
		NFFT=wsize,
		Fs=Fs,
		window=mlab.window_hanning,
		noverlap=int(wsize * wratio))[0]

	# apply log transform since specgram() returns linear array
	arr2D = 10 * np.log10(arr2D)
	arr2D[arr2D == -np.inf] = 0  # replace infs with zeros

	# find local maxima
	local_maxima = get_2D_peaks(arr2D, plot=False, amp_min=amp_min)

	local_maxima.sort()
	return local_maxima

	# return hashes
	#return generate_hashes(local_maxima, fan_value=fan_value)


def get_2D_peaks(arr2D, plot=True, amp_min=DEFAULT_AMP_MIN):
	# http://docs.scipy.org/doc/scipy/reference/generated/scipy.ndimage.morphology.iterate_structure.html#scipy.ndimage.morphology.iterate_structure
	struct = generate_binary_structure(2, 1)
	neighborhood = iterate_structure(struct, PEAK_NEIGHBORHOOD_SIZE)

	# find local maxima using our fliter shape
	local_max = maximum_filter(arr2D, footprint=neighborhood) == arr2D
	background = (arr2D == 0)
	eroded_background = binary_erosion(background, structure=neighborhood,
									   border_value=1)

	# Boolean mask of arr2D with True at peaks
	detected_peaks = local_max - eroded_background

	# extract peaks
	amps = arr2D[detected_peaks]
	j, i = np.where(detected_peaks)

	# filter peaks
	amps = amps.flatten()
	peaks = zip(i, j, amps)
	peaks_filtered = [x for x in peaks if x[2] > amp_min]  # freq, time, amp

	# get indices for frequency and time
	frequency_idx = [x[1] for x in peaks_filtered]
	time_idx = [x[0] for x in peaks_filtered]

	plot = False
	if plot:
		# scatter of the peaks
		fig, ax = plt.subplots()
		ax.imshow(arr2D)
		ax.scatter(time_idx, frequency_idx)
		ax.set_xlabel('Time')
		ax.set_ylabel('Frequency')
		ax.set_title("Spectrogram")
		plt.gca().invert_yaxis()
		plt.show()

	return zip(time_idx, frequency_idx)


def findpeaks(filename, music, limit=None, song_name=None):
	# Calculates peaks for given music file.

    try:
        filename, limit = filename
    except ValueError:
        pass

    songname, extension = os.path.splitext(os.path.basename(filename))

    song_name = song_name or songname

    channels, Fs, duration = decoder.read(filename, limit)

    result = set()

    channel_amount = len(channels)
    for channeln, channel in enumerate(channels):
        # TODO: Remove prints or change them into optional logging.
        print("Fingerprinting channel %d/%d for %s" % (channeln + 1,
                                                       channel_amount,
                                                       filename))
        music.append( fingerprint(channel, Fs=Fs) )
        print("Finished channel %d/%d for %s" % (channeln + 1, channel_amount,
                                                 filename))

    return duration
