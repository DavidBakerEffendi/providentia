/******************************************************************************
 * Copyright (c) 2015-2016, TigerGraph Inc.
 * All rights reserved.
 * Project: TigerGraph Query Language
 * udf.hpp: a library of user defined functions used in queries.
 *
 * - This library should only define functions that will be used in
 *   TigerGraph Query scripts. Other logics, such as structs and helper
 *   functions that will not be directly called in the GQuery scripts,
 *   must be put into "ExprUtil.hpp" under the same directory where
 *   this file is located.
 *
 * - Supported type of return value and parameters
 *     - int
 *     - float
 *     - double
 *     - bool
 *     - string (don't use std::string)
 *     - accumulators
 *
 * - Function names are case sensitive, unique, and can't be conflict with
 *   built-in math functions and reserve keywords.
 *
 * - Please don't remove necessary codes in this file
 *
 * - A backup of this file can be retrieved at
 *     <tigergraph_root_path>/dev_<backup_time>/gdk/gsql/src/QueryUdf/ExprFunctions.hpp
 *   after upgrading the system.
 *
 ******************************************************************************/

#ifndef EXPRFUNCTIONS_HPP_
#define EXPRFUNCTIONS_HPP_

#include <stdlib.h>
#include <stdio.h>
#include <string>
#include <gle/engine/cpplib/headers.hpp>

#include "ExprUtil.hpp"

/**     XXX Warning!! Put self-defined struct in ExprUtil.hpp **
 *  No user defined struct, helper functions (that will not be directly called
 *  in the GQuery scripts) etc. are allowed in this file. This file only
 *  contains user-defined expression function's signature and body.
 *  Please put user defined structs, helper functions etc. in ExprUtil.hpp
 */

namespace UDIMPL
{

typedef std::string string;

/****** BUILT-IN FUNCTIONS **************/
inline int str_to_int(string str)
{
  return atoi(str.c_str());
}

inline int float_to_int(float val)
{
  return (int) val;
}

inline string to_string(double val)
{
  char result[200];
  sprintf(result, "%g", val);
  return string(result);
}

/****** CUSTOM FUNCTIONS **************/

/**
 * Returns a grid ID for the given x and y coordinates.
 */
inline string getGridId(float x, float y)
{
  return map_cartesian_to_grid_id(x, y);
}

/**
 * Returns an set of all grid IDs of grids that are within the radius specified by distKm
 */
inline SetAccum<string> getNearbyGridId(double distKm, double x, double y)
{
  string gridIdStr = map_cartesian_to_grid_id(x, y);
  uint64_t gridId = atoi(gridIdStr.c_str());

  int dia_long = gridNumX(distKm);
  int dia_lat = gridNumY(distKm);

  int minus_dia_long = -1 * dia_long;
  int minus_dia_lat = -1 * dia_lat;

  SetAccum<string> result;

  result += gridIdStr;

  int origin_lat = gridId / NUM_OF_COLS;
  int origin_lon = gridId % NUM_OF_COLS;

  for (int i = minus_dia_lat; i <= dia_lat; i++)
  {
    for (int j = minus_dia_long; j <= dia_long; j++)
    {
      int new_lat = origin_lat + i;
      int new_lon = origin_lon + j;

      // wrap around
      if (new_lat < 0)
      {
        new_lat = NUM_OF_ROWS + new_lat;
      }
      else if (new_lat > NUM_OF_ROWS)
      {
        new_lat = new_lat - NUM_OF_ROWS;
      }

      if (new_lon < 0)
      {
        new_lon = NUM_OF_COLS + new_lon;
      }
      else if (new_lon > NUM_OF_COLS)
      {
        new_lon = new_lon - NUM_OF_COLS;
      }

      int id = new_lon + NUM_OF_COLS * new_lat;

      result += std::to_string(id);
    }
  }
  return result;
}

/**
 * Calculate the Euclidean distance between two points.
 */
inline double geoDistance(double y_from, double x_from, double y_to, double x_to)
{
  double dx = x_to - x_from;
  double dy = y_to - x_from;
  return dx * dx + dy * dy;
}

} // namespace UDIMPL
/****************************************/

#endif /* EXPRFUNCTIONS_HPP_ */
