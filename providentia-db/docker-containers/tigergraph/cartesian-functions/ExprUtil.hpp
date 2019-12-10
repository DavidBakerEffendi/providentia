/******************************************************************************
 * Copyright (c) 2016, TigerGraph Inc.
 * All rights reserved.
 * Project: TigerGraph Query Language
 *
 * - This library is for defining struct and helper functions that will be used
 *   in the user-defined functions in "ExprFunctions.hpp". Note that functions
 *   defined in this file cannot be directly called from TigerGraph Query scripts.
 *   Please put such functions into "ExprFunctions.hpp" under the same directory
 *   where this file is located.
 *
 * - Please don't remove necessary codes in this file
 *
 * - A backup of this file can be retrieved at
 *     <tigergraph_root_path>/dev_<backup_time>/gdk/gsql/src/QueryUdf/ExprUtil.hpp
 *   after upgrading the system.
 *
 ******************************************************************************/

#ifndef EXPRUTIL_HPP_
#define EXPRUTIL_HPP_

#include <stdlib.h>
#include <stdio.h>
#include <string>
#include <gle/engine/cpplib/headers.hpp>

typedef std::string string;

/*
 * Define structs that used in the functions in "ExprFunctions.hpp"
 * below. For example,
 *
 *   struct Person {
 *     string name;
 *     int age;
 *     double height;
 *     double weight;
 *   }
 *
 */

/*******************************************************
 * Config this for grid size. The number is in meters.
 * The area of a geo grid is the square of GRID_SIDE_LENGTH.
 ******************************************************/
static const int GRID_SIDE_LENGTH = 100;

static const int MAP_Y_LOW_BOUND = 0;  // Low Bound of km of the map;
static const int MAP_Y_HIGH_BOUND = 2000; // High Bound of km of the map;
static const int MAP_X_LOW_BOUND = 0;  // Low Bound of km of the map;
static const int MAP_X_HIGH_BOUND = 2000; // High Bound of km of the map;
// Calculate the number of rows and columns where they are divided into
// GRID_SIDE_LENGTH dimensions. This requires converting back to meters.
static const int NUM_OF_COLS = (MAP_X_HIGH_BOUND * 1000 - MAP_X_LOW_BOUND * 1000) / GRID_SIDE_LENGTH;
static const int NUM_OF_ROWS = (MAP_Y_HIGH_BOUND * 1000 - MAP_Y_LOW_BOUND * 1000) / GRID_SIDE_LENGTH;
// Expected height and width of the grid in meters
static const int GRID_HEIGHT = (std::abs(MAP_Y_HIGH_BOUND) + std::abs(MAP_Y_LOW_BOUND)) * 1000;
static const int GRID_WIDTH = (std::abs(MAP_X_HIGH_BOUND) + std::abs(MAP_X_LOW_BOUND)) * 1000;

inline float km2YPoint(float km)
{
  return km * 1000;
}

inline float km2XPoint(float km)
{
  return km * 1000;
}

inline int gridNumY(float km)
{
  return std::abs(round(km2YPoint(km) / (GRID_WIDTH / NUM_OF_ROWS)));
}

inline int gridNumX(float km)
{
  return std::abs(round(km2XPoint(km) / (GRID_HEIGHT / NUM_OF_COLS)));
}

/**
 * Converts an x and y (meters) into a grid ID
 */
inline string map_cartesian_to_grid_id(double x, double y)
{
  int64_t grid_id = (y - MAP_Y_LOW_BOUND * 1000) / GRID_SIDE_LENGTH * NUM_OF_COLS + (x - MAP_X_LOW_BOUND * 1000) / GRID_SIDE_LENGTH;

  return std::to_string(grid_id);
}

#endif /* EXPRUTIL_HPP_ */
