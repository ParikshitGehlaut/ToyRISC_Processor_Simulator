import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

width_values = [100, 200, 300, 400, 500, 600, 700, 800, 900, 1000]
probability_values = [0.1, 0.3, 0.5, 0.7, 0.9]
time_taken_values = [
    [865, 802, 433, 109, 469],
    [1504, 1783, 1603, 1639, 1396],
    [2683, 2062, 2503, 2449, 2215],
    [3052, 3583, 3331, 2791, 2755],
    [3619, 3709, 4078, 4006, 3943],
    [4591, 5059, 4798, 5392, 4555],
    [5716, 6013, 5536, 5914, 6076],
    [7192, 6373, 7075, 6553, 7174],
    [7237, 7777, 8101, 7552, 7543],
    [8740, 8137, 8866, 8542, 8146]
]

width_mesh, prob_mesh = np.meshgrid(width_values, probability_values)
time_taken_values = np.array(time_taken_values).T
# Plot graph between width and time taken
fig = plt.figure(figsize=(12, 6))
ax1 = fig.add_subplot(111, projection='3d')
ax1.plot_surface(width_mesh, prob_mesh, time_taken_values, cmap='plasma')
ax1.set_xlabel('Width')
ax1.set_ylabel('Probability')
ax1.set_zlabel('Time Taken (seconds)')
ax1.set_title('Width vs Probability vs Time Taken')

plt.show()


