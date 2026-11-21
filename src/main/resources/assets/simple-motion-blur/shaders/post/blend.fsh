#version 330

// CurrentSampler = the frame that was just rendered
// PrevSampler    = the blurred result from the previous frame (history)
uniform sampler2D CurrentSampler;
uniform sampler2D PrevSampler;

layout(std140) uniform BlendConfig {
    float Strength; // how much of the previous frame to keep (0..1)
};

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec3 current = texture(CurrentSampler, texCoord).rgb;
    vec4 prev = texture(PrevSampler, texCoord);

    // History starts empty (alpha 0). On the very first frame just show the current frame.
    float hasHistory = step(0.5, prev.a);

    fragColor = vec4(mix(current, prev.rgb, Strength * hasHistory), 1.0);
}
