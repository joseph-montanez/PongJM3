uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
uniform mat3 g_NormalMatrix;
uniform mat4 g_ViewMatrix;

attribute vec3 inPosition;

void main(void)
{
    vec4 pos = vec4(inPosition, 1.0);
    vec3 wvPosition = (g_WorldViewMatrix * pos).xyz;
    vPos = wvPosition;
    gl_Position = gl_Vertex;
}