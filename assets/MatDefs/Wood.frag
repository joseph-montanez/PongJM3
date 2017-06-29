// precision mediump float;

varying vec2 position;
varying vec2 vPos;
uniform mat4 g_WorldViewProjectionMatrix;
uniform float g_Time;

const float waves = 19.0;

// triangle wave from 0 to 1
float wrap(float n) {
  return abs(mod(n, 2.)-1.)*-1. + 1.;
}

// creates a cosine wave in the plane at a given angle
float wave(float angle, vec2 point) {
  float cth = cos(angle);
  float sth = sin(angle);
  return (cos (cth*point.x + sth*point.y) + 1.) / 2.;
}

// sum cosine waves at various interfering angles
// wrap values when they exceed 1
float quasi(float interferenceAngle, vec2 point) {
  float sum = 0.;
  for (float i = 0.; i < waves; i++) {
    sum += wave(3.1416 * i * interferenceAngle, point);
  }
  return wrap(sum);
}

void main() {
  float b = quasi(g_Time * 0.002, (vPos - 0.5) * 200.0);
  vec4 c1 = vec4(0.0, 0.0, 0.2, 1.0);
  vec4 c2 = vec4(1.5, 0.7 ,0.0, 1.0);
  gl_FragColor = mix(c1, c2, b);
}