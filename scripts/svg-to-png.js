#!/usr/bin/env node
/**
 * Rasterize an SVG to a 1024x500 PNG (Play Store feature graphic size).
 *
 * Usage:
 *   node scripts/svg-to-png.js [input.svg] [output.png] [width] [height]
 *
 * Defaults:
 *   input   designs/play-store-feature-graphic.svg
 *   output  designs/play-store-feature-graphic.png
 *   width   1024
 *   height  500
 *
 * Requires: npm install sharp
 */
const path = require("path");
const sharp = require("sharp");

const repoRoot = path.resolve(__dirname, "..");

const input = process.argv[2] || path.join(repoRoot, "designs", "play-store-feature-graphic.svg");
const output = process.argv[3] || path.join(repoRoot, "designs", "play-store-feature-graphic.png");
const width = Number(process.argv[4]) || 1024;
const height = Number(process.argv[5]) || 500;

// density (DPI) controls how finely the SVG is rasterized before any resize.
// A high value keeps strokes/text crisp; the final .resize() pins exact pixels.
const density = 300;

sharp(input, { density })
  .resize(width, height, { fit: "fill" })
  .png()
  .toFile(output)
  .then((info) => {
    console.log(`Wrote ${output} (${info.width}x${info.height}, ${info.size} bytes)`);
  })
  .catch((err) => {
    console.error("Failed to convert SVG:", err.message);
    process.exit(1);
  });
